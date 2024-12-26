
package com.ms.jedi.dl.core.harvester

import java.util.UUID.randomUUID

import com.databricks.spark.xml.XmlRelation
import org.apache.spark.sql.catalyst.expressions.{AttributeReference, SortOrder, Attribute => SparkAttribute}
import org.apache.spark.sql.catalyst.plans.logical._
import org.apache.spark.sql.execution.datasources.{DataSource, HadoopFsRelation, LogicalRelation}
import org.apache.spark.sql.sources.BaseRelation
import org.apache.spark.sql.SaveMode
import com.ms.jedi.dl.model.{op, _}

import com.ms.jedi.dl.core.harvester._
import com.ms.jedi.dl.coreadapterapi.SaveAsTableCommand
import com.ms.jedi.dl.coreadapterapi.SaveJDBCCommand
import com.ms.jedi.dl.coreadapterapi.WriteCommand
//import com.ms.jedi.dl.coreapadterapi.sql.JDBCRelation



sealed trait OperationNodeBuilder {

  val operation: LogicalPlan

  private var childBuilders: Seq[OperationNodeBuilder] = Nil

  protected val output: AttrGroup = new AttrGroup(operation.output)

  def +=(childBuilder: OperationNodeBuilder): Unit = childBuilders :+= childBuilder

  def build(): op.Operation

  protected def componentCreatorFactory: ComponentCreatorFactory

  protected def attributeCreator: AttributeConverter = componentCreatorFactory.attributeConverter

  protected def expressionCreator: ExpressionConverter = componentCreatorFactory.expressionConverter

  protected def metaDatasetCreator: MetaDatasetConverter = componentCreatorFactory.metaDatasetConverter

  protected def operationProps = op.OperationProps(
    randomUUID,
    operation.nodeName,
    childBuilders.map(b => metaDatasetCreator.convert(b.output).id),
    metaDatasetCreator.convert(output).id
  )
}

class AttrGroup(val attrs: Seq[SparkAttribute])

class GenericNodeBuilder
(val operation: LogicalPlan)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  override def build() = op.Generic(operationProps, operation.verboseString)
}

class AliasNodeBuilder
(val operation: SubqueryAlias)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  override def build() = op.Alias(operationProps, operation.alias)
}

trait FSAwareBuilder {
  protected def getQualifiedPath(path: String): String
}

class ReadNodeBuilder
(val operation: LogicalRelation)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  this: FSAwareBuilder =>

  override def build(): op.Read = {
    val (sourceType, paths) = getRelationPaths(operation.relation)
    op.Read(
      operationProps,
      sourceType,
      paths.map(MetaDataSource(_, Nil))
    )
  }

  private def getRelationPaths(relation: BaseRelation): (String, Seq[String]) = relation match {
    case HadoopFsRelation(loc, _, _, _, fileFormat, _) => (
      fileFormat.toString,
      loc.rootPaths.map(path => getQualifiedPath(path.toString))
    )
    case XmlRelation(_, loc, _, _) => (
      "XML",
      loc.toSeq map getQualifiedPath
    )
    /*case JDBCRelation(jdbcOpts) => (
      "JDBC",
      Seq(s"${jdbcOpts.url}/${jdbcOpts.table}")
    )*/
    case _ => // unrecognized relation type
      (s"???: ${relation.getClass.getName}", Nil)
  }
}

abstract class WriteNodeBuilder
(val operation: WriteCommand, val writeMetrics: Map[String, Long], val readMetrics: Map[String, Long])
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder with RootNode {
  this: FSAwareBuilder =>

  override val output: AttrGroup = new AttrGroup(operation.query.output)

  override def build() = op.Write(
    operationProps,
    operation.format,
    getQualifiedPath(operation.path),
    append = operation.mode == SaveMode.Append,
    writeMetrics = writeMetrics,
    readMetrics = readMetrics
  )

  override def ignoreLineageWrite:Boolean = {
    writeMetrics.get("numFiles").filter(0.==).isDefined
  }
}

class SaveAsTableNodeBuilder
(val operation: SaveAsTableCommand, val writeMetrics: Map[String, Long], val readMetrics: Map[String, Long])
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder with RootNode {

  override val output: AttrGroup = new AttrGroup(operation.query.output)

  override def build() = op.Write(
    operationProps,
    operation.format,
    operation.tableName,
    append = operation.mode == SaveMode.Append,
    writeMetrics = writeMetrics,
    readMetrics = readMetrics
  )

  override def ignoreLineageWrite:Boolean = {
    false
  }
}

class SaveJDBCCommandNodeBuilder
(val operation: SaveJDBCCommand, val writeMetrics: Map[String, Long], val readMetrics: Map[String, Long])
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder with RootNode {

  override val output: AttrGroup = new AttrGroup(operation.query.output)

  override def build() = op.Write(
    operationProps,
    operation.format,
    operation.tableName,
    append = operation.mode == SaveMode.Append,
    writeMetrics = writeMetrics,
    readMetrics = readMetrics
  )

  override def ignoreLineageWrite:Boolean = {
    false
  }
}

trait RootNode {
  def ignoreLineageWrite:Boolean
}

class ProjectionNodeBuilder
(val operation: Project)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  override def build(): op.Projection = {
    val transformations = operation.projectList
      .filterNot(_.isInstanceOf[AttributeReference])
      .map(expressionCreator.convert)

    op.Projection(
      operationProps,
      transformations)
  }
}

class FilterNodeBuilder
(val operation: Filter)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  override def build() = op.Filter(
    operationProps,
    expressionCreator.convert(operation.condition))
}

class SortNodeBuilder
(val operation: Sort)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  override def build() = op.Sort(
    operationProps,
    for (SortOrder(expression, direction, nullOrdering, _) <- operation.order)
      yield op.SortOrder(expressionCreator.convert(expression), direction.sql, nullOrdering.sql)
  )
}

class AggregateNodeBuilder
(val operation: Aggregate)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  override def build() = op.Aggregate(
    operationProps,
    operation.groupingExpressions map expressionCreator.convert,
    operation.aggregateExpressions.map(namedExpr =>
      namedExpr.name -> expressionCreator.convert(namedExpr)).toMap
  )
}

class JoinNodeBuilder
(val operation: Join)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  override def build() = op.Join(
    operationProps,
    operation.condition map expressionCreator.convert,
    operation.joinType.toString)
}

class UnionNodeBuilder
(val operation: Union)
(implicit val componentCreatorFactory: ComponentCreatorFactory)
  extends OperationNodeBuilder {
  override def build() = op.Union(operationProps)
}


