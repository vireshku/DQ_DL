/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.persistence.dao

import java.util.UUID
import java.util.function.{ Consumer, Predicate }
import java.{ util => ju }
import com.mongodb.casbah.query.Implicits.mongoQueryStatements
import com.mongodb.{ BasicDBList, BasicDBObject, DBObject }
import org.apache.commons.lang.StringUtils
import salat.{ BinaryTypeHintStrategy, TypeHintFrequency }
import com.ms.jedi.dl.common.EnumerationMacros.sealedInstancesOf
import com.ms.jedi.dl.common.transformations.{ AbstractConverter, CachingConverter }
import com.ms.jedi.dl.persistence.api.CloseableIterable
import com.ms.jedi.dl.persistence.impl.DBObjectImplicits._
import com.ms.jedi.dl.persistence.dao.BaselineLineageDAO.Component
import com.ms.jedi.dl.persistence.dao.BaselineLineageDAO.Component.SubComponent
import com.ms.jedi.dl.persistence.dao.LineageDAOv4.{ Field, SubComponentV4 }
import com.ms.jedi.dl.persistence.serialise.BSONSalatContext
import scala.collection.JavaConverters._
import scala.concurrent.{ ExecutionContext, Future }
import com.ms.jedi.dl.persistence.impl.MongoConnection

class LineageDAOv4(override val connection: MongoConnection) extends BaselineLineageDAO with MutableLineageUpgraderV4 {

  import LineageDAOv4._

  override val version: Int = 4

  override protected lazy val subComponents: Seq[SubComponent] =
    SubComponent.values ++ SubComponentV4.values

  override def save(lineage: DBObject)(implicit e: ExecutionContext): Future[Unit] = {
    println("LineageDAOv4 -----------------> ")
    lineage.put(SubComponentV4.Transformation.name, extractTransformationsFromLineage(lineage))
    super.save(lineage)
  }

  private def extractTransformationsFromLineage(lineage: DBObject) = {
    val operations = lineage.get(Component.Operation.name).asInstanceOf[Seq[DBObject]]
    (Seq.empty[DBObject] /: operations.view) {
      case (transformationsAcc, op: DBObject) if isProjectOperation(op) =>
        val augmentedTransformations = {
          val opId = getOperationId(op)
          val opTransformations = op.removeField(SubComponentV4.Transformation.name).asInstanceOf[Seq[DBObject]]
          opTransformations.map(expr => {
            expr.put(Field.opId, opId)
            expr
          })
        }
        transformationsAcc ++ augmentedTransformations

      case (transformationPOsAcc, _) => transformationPOsAcc
    }
  }

  override protected def addComponents(rootComponentDBO: DBObject, overviewOnly: Boolean)(implicit ec: ExecutionContext): Future[DBObject] = {
    val eventualLineageDBO = super.addComponents(rootComponentDBO, overviewOnly)
    if (overviewOnly)
      eventualLineageDBO
    else {
      eventualLineageDBO.map(lineage => {
        val operations = lineage.get(Component.Operation.name).asInstanceOf[ju.List[DBObject]]
        enforceDefaultMetricsOnWriteOperation(operations.get(0))

        val transformations = lineage.get(SubComponentV4.Transformation.name).asInstanceOf[ju.List[DBObject]]
        insertTransformationsIntoLineage(transformations.asScala, lineage)
      })
    }
  }

  override protected val overviewComponentFilter: PartialFunction[Component.SubComponent, DBObject] = {
    case Component.Operation =>
      Field.t $in Seq(
        "com.ms.jedi.dl.model.op.Read",
        "com.ms.jedi.dl.model.op.Write")
        .map(binaryTypeHintStrategy.encode)
  }

  private def enforceDefaultMetricsOnWriteOperation(writeOp: DBObject): Unit = {
    writeOp.putIfAbsent(Field.readMetrics, new BasicDBObject(new ju.HashMap()))
    writeOp.putIfAbsent(Field.writeMetrics, new BasicDBObject(new ju.HashMap()))
  }

  private def insertTransformationsIntoLineage(transformations: Seq[DBObject], lineage: DBObject) = {
    val transformationsByOperationId = transformations.groupBy(_.get(Field.opId))
    val operations = lineage.get(Component.Operation.name).asInstanceOf[ju.List[DBObject]]
    operations.forEach(new Consumer[DBObject] {
      override def accept(op: DBObject): Unit =
        if (isProjectOperation(op)) {
          val opId = getOperationId(op)
          val opTransformations = transformationsByOperationId.getOrElse(opId, Nil)
          op.put(SubComponentV4.Transformation.name, new BasicDBList {
            addAll(opTransformations.asJava)
          })
        }
    })
    lineage
  }

  private def isProjectOperation(op: DBObject): Boolean = {
    val hintStrategy = BSONSalatContext.ctx.typeHintStrategy
    val opClassName = hintStrategy.decode(op.get(hintStrategy.typeHint))
    opClassName.endsWith("op.Projection")
  }

  private def getOperationId(op: DBObject) =
    op.get(Field.mainProps).asInstanceOf[DBObject].get(Field.id).asInstanceOf[UUID]
}

object LineageDAOv4 {

  val binaryTypeHintStrategy = BinaryTypeHintStrategy(TypeHintFrequency.Always)

  object Field {
    val child = "child"
    val children = "children"

    val condition = "condition"
    val aggregations = "aggregations"
    val groupings = "groupings"
    val transformations = "transformations"
    val orders = "orders"
    val expression = "expression"

    val t = "_t"
    val typeHint = "_typeHint"
    val id = "_id"
    val opId = "_opId"
    val sparkVer = "sparkVer"

    val mainProps = "mainProps"
    val datasetId = "datasetId"
    val dataType = "dataType"
    val dataTypeId = "dataTypeId"
    val elementDataType = "elementDataType"
    val elementDataTypeId = "elementDataTypeId"
    val fields = "fields"
    val text = "text"
    val value = "value"
    val name = "name"
    val exprType = "exprType"

    val writeMetrics = "writeMetrics"
    val readMetrics = "readMetrics"
  }

  object SubComponentV4 {
    sealed trait SubComponentV4 extends SubComponent
    case object Transformation extends Component("transformations") with SubComponentV4
    case object DataType extends Component("dataTypes") with SubComponentV4
    val values: Seq[SubComponent] = sealedInstancesOf[SubComponentV4].toSeq
  }

}

trait MutableLineageUpgraderV4 {
  this: VersionedLineageDAO =>

  import MutableLineageUpgraderV4._

  override def upgrader = Some(new VersionUpgrader {
    override def versionFrom: Int = 3

    override def apply[T](data: T)(implicit ec: ExecutionContext): Future[T] = data match {
      case None | _: UUID | _: Number => Future.successful(data)

      case Some(o) => apply(o).map(Some(_).asInstanceOf[T])

      case iterable: CloseableIterable[_] if iterable.iterator.isEmpty => Future.successful(data)

      case iterable: CloseableIterable[_] =>
        Future.traverse(iterable.iterator)(apply).
          map(new CloseableIterable(_, iterable.close()).asInstanceOf[T])

      case lineage: DBObject if (lineage get Field.id).toString startsWith "ln_" =>
        if (lineage containsField Field.datasetId)
          Future.successful(data) // it's a DatasetDescription, no upgrade required
        else {
          upgradeLineage(lineage)
          Future.successful(lineage.asInstanceOf[T])
        }
    }
  })
}

object MutableLineageUpgraderV4 {

  class DataTypeConverter extends AbstractConverter {
    override type From = BasicDBObject
    override type To = BasicDBObject

    override def convert(originalDT: BasicDBObject): BasicDBObject = {
      val dt = new BasicDBObject(originalDT)
      dt.put(Field.id, UUID.randomUUID)
      getDataTypeType(dt) match {
        case "Array" =>
          convertAndReplace(dt, Field.elementDataType, Field.elementDataTypeId)
        case "Struct" =>
          val upgradedFields =
            for (field <- dt.get(Field.fields).asInstanceOf[ju.List[BasicDBObject]].asScala)
              yield convertAndReplace(new BasicDBObject(field), Field.dataType, Field.dataTypeId)
          dt.replace(Field.fields, new BasicDBList {
            addAll(upgradedFields.asJava)
          })
        case _ =>
      }
      upgradeTypeHintOf(dt)
      dt
    }

    def convertAndReplace(o: DBObject, srcField: String, dstField: String): DBObject = {
      val originalDT = o.removeField(srcField).asInstanceOf[BasicDBObject]
      if (originalDT != null) {
        val upgradedDT = convert(originalDT)
        o.put(dstField, upgradedDT.get(Field.id))
      }
      o
    }
  }

  private def upgradeLineage(lineage: DBObject): Unit = {
    val dtConverter = new DataTypeConverter with CachingConverter

    lineage.put(Field.sparkVer, "2.x")

    // ... attributes
    lineage.get(Component.Attribute.name).asInstanceOf[ju.List[DBObject]].asScala.foreach(upgradeDataTypeOf)

    // ... operations
    for (op <- lineage.get(Component.Operation.name).asInstanceOf[ju.List[DBObject]].asScala) {
      val opType = getOperationType(op)
      opType match {
        case "Join" | "Filter" =>
          val expr = op.get(Field.condition).asInstanceOf[BasicDBObject]
          if (expr != null) upgradeExpression(expr)
        case "Aggregate" =>
          op.get(Field.aggregations).asInstanceOf[ju.Map[String, DBObject]].
            values.asScala.foreach(upgradeExpression)
          op.get(Field.groupings).asInstanceOf[ju.List[DBObject]].asScala.
            foreach(upgradeExpression)
        case "Projection" =>
          val transformations = op.get(Field.transformations).asInstanceOf[ju.List[DBObject]]
          transformations.removeIf(new Predicate[DBObject] {
            override def test(t: DBObject): Boolean = getExpressionType(t) == "AttributeRemoval"
          })
          transformations.asScala.
            foreach(upgradeExpression)
        case "Sort" =>
          op.get(Field.orders).asInstanceOf[ju.List[DBObject]].asScala.
            foreach(order => upgradeExpression(order.get(Field.expression).asInstanceOf[DBObject]))
        case _ =>
      }
      upgradeTypeHintOf(op)
    }

    // ... dataTypes
    lineage.put(SubComponentV4.DataType.name, new BasicDBList {
      addAll(dtConverter.values.asJava)
    })

    def upgradeExpression(expr: DBObject): Unit = {
      import com.mongodb.casbah.Implicits._

      for {
        children <- expr.getAs[Seq[DBObject]](Field.children)
        child <- children
      } upgradeExpression(child)

      getExpressionType(expr) match {
        case "UserDefinedFunction" =>
          renameExpressionType(expr, "UDF")
        case "AttributeReference" =>
          renameExpressionType(expr, "AttrRef")
          expr.removeField(Field.dataType)
        case "Alias" =>
          expr.put(Field.child, expr.get(Field.children).asInstanceOf[ju.List[_]].get(0))
          expr.removeField(Field.dataType)
        case "Generic" if expr.get(Field.exprType) == "Literal" =>
          expr.removeField(Field.exprType)
          expr.put(Field.value, expr.removeField(Field.text))
          renameExpressionType(expr, "Literal")
        case "Generic" =>
          expr.put(Field.name, StringUtils.substringBefore(expr.removeField(Field.text).toString, "(").trim)
          val children = expr.get(Field.children).asInstanceOf[ju.List[_]]
          if (children == null || children.isEmpty)
            renameExpressionType(expr, "GenericLeaf")
        case _ =>
      }

      upgradeDataTypeOf(expr)
      upgradeTypeHintOf(expr)
    }

    def renameExpressionType(expr: DBObject, typeName: String): Unit =
      expr.put(Field.typeHint, "com.ms.jedi.dl.model.expr." + typeName)

    def upgradeDataTypeOf(o: DBObject): Unit =
      dtConverter.convertAndReplace(o, Field.dataType, Field.dataTypeId)
  }

  private def upgradeTypeHintOf(o: DBObject): Unit = {
    val stringTypeHint = o.removeField(Field.typeHint).toString
    val binaryTypeHint = LineageDAOv4.binaryTypeHintStrategy.encode(stringTypeHint)
    o.put(Field.t, binaryTypeHint)
  }

  private def getOperationType(op: DBObject) = op.get(Field.typeHint).toString.substring(27)

  private def getDataTypeType(dt: DBObject) = dt.get(Field.typeHint).toString.substring(27)

  private def getExpressionType(ex: DBObject) = ex.get(Field.typeHint).toString.substring(29)
}
