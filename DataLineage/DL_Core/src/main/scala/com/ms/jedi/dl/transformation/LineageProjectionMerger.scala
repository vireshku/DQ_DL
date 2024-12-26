/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.transformation

import java.util.UUID
import java.util.UUID.randomUUID
import scala.concurrent.{ ExecutionContext, Future }
import com.ms.jedi.dl.model.{ Attribute, DataLineage }
import com.ms.jedi.dl.common.transformations.AsyncTransformation
import com.ms.jedi.dl.model.op.{ Operation, OperationProps, Projection }
import com.ms.jedi.dl.model.expr.{ Alias, AttrRef, Expression }

/**
 * The object is responsible for the logic that merges compatible projections into one node within lineage graph.
 */
object LineageProjectionMerger extends AsyncTransformation[DataLineage] {

  private val pipeline = Seq(
    mergeProjections _,
    cleanupReferences _)

  /**
   * The method transforms an input instance by a custom logic.
   *
   * @param lineage An input instance
   * @return A transformed result
   */
  override def apply(lineage: DataLineage)(implicit ec: ExecutionContext): Future[DataLineage] =
    {
      println("LineageProjectionMerger----------------> apply ------------->")
      Future.successful((lineage /: pipeline)((lin, f) => f(lin)))
    }

  def cleanupReferences(lineage: DataLineage): DataLineage = {
    println("cleanupReferences--------------------------->")
    lineage.rectified
  }

  def mergeProjections(lineage: DataLineage): DataLineage = {

    println("mergeProjections started--------------------------->")
    val attributeById = lineage.attributes.map(attr => attr.id -> attr).toMap
    val mergedOperations = lineage.operations.foldLeft(List.empty[Operation])(
      (collection, value) => collection match {
        case Nil => List(value)
        case x :: xs =>
          if (canMerge(x, value, lineage.operations, attributeById))
            merge(x, value) :: xs
          else
            value :: collection
      }).reverse

    println("mergeProjections Finished--------------------------->")
    lineage.copy(operations = mergedOperations)
  }

  private def canMerge(a: Operation, b: Operation, allOperations: Seq[Operation], attributesById: Map[UUID, Attribute]): Boolean = {
    def transformationsAreCompatible(ats: Seq[Expression], bts: Seq[Expression]) = {
      val inputAttributeNames = ats.
        flatMap(_.allRefLikeChildrenFlattened).
        flatMap({
          case ref: AttrRef => Some(attributesById(ref.refId).name)
          case _            => None
        })

      val outputAttributeNames = bts.
        flatMap(_.allRefLikeChildrenFlattened).
        flatMap({
          case alias: Alias => Some(alias.alias)
          case _            => None
        })

      (inputAttributeNames intersect outputAttributeNames).isEmpty
    }

    (a, b) match {
      case (Projection(am, ats), Projection(bm, bts)) if am.inputs.length == 1
        && am.inputs.head == bm.output
        && (allOperations.flatMap(_.mainProps.inputs) count bm.output.==) == 1 =>
        transformationsAreCompatible(ats, bts)
      case _ => false
    }
  }

  private def merge(a: Operation, b: Operation): Operation = {
    val mainPropsA = a.mainProps
    val mainPropsB = b.mainProps
    val projectNodeA = a.asInstanceOf[Projection]
    val projectNodeB = b.asInstanceOf[Projection]
    val node = Projection(
      OperationProps(
        randomUUID,
        mainPropsB.name,
        mainPropsB.inputs,
        mainPropsA.output),
      projectNodeB.transformations ++ projectNodeA.transformations)

    node
  }
}
