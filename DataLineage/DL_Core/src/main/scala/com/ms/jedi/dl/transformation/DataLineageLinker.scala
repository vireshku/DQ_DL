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

import org.slf4s.Logging
import com.ms.jedi.dl.common.ARM._

import com.ms.jedi.dl.model.op.{ Operation, Read }
import com.ms.jedi.dl.model.{ DataLineage, LinkedLineage, MetaDataSource }

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.postfixOps
import com.ms.jedi.dl.model.DataLineage
import com.ms.jedi.dl.persistence.api.DataLineageReader
import com.ms.jedi.dl.common.transformations.AsyncTransformation

/**
 * The class injects into a lineage graph root meta data sets from related lineage graphs.
 *
 * @param reader A reader reading lineage graphs from persistence layer
 */
class DataLineageLinker(reader: DataLineageReader) extends AsyncTransformation[DataLineage] with Logging {

  /**
   * The method transforms an input instance by a custom logic.
   *
   * @param lineage An input lineage graph
   * @return A transformed result
   */
  override def apply(lineage: DataLineage)(implicit ec: ExecutionContext): Future[DataLineage] = {
    println("DataLineageLinker <----------------->  apply")
    def castIfRead(op: Operation): Option[Read] = op match {
      case a @ Read(_, _, _) => Some(a)
      case _                 => None
    }

    def resolveMetaDataSources(mds: MetaDataSource): Future[MetaDataSource] = {
      println("resolveMetaDataSources------->" + s"Resolving lineage of ${mds.path}")

      println("mds.datasetsIds.   --- > " + mds.datasetsIds.mkString("<===>"))

      assume(mds.datasetsIds.isEmpty, s"a lineage of ${mds.path} is yet to be found")
      
      println("resolveMetaDataSources------->  crossed Assume")

      reader.findLatestDatasetIdsByPath(mds.path) map managed(dsIdCursor => {
        val dsIds = dsIdCursor.iterator.toList
        if (dsIds.isEmpty)
          println(s"Lineage of ${mds.path} NOT FOUND")
        else
          println(s"Lineage of ${mds.path}  FOUND  ----> " + dsIds.mkString("<===============>"))
        mds.copy(datasetsIds = dsIds)
      })
    }

    val eventualReadsWithLineages: Future[Seq[Read]] = Future.sequence(
      for {
        op <- lineage.operations
        read <- castIfRead(op)
        eventualSources = Future.sequence(read.sources map resolveMetaDataSources)
      } yield eventualSources map (newSources => {
        val newProps = read.mainProps.copy(inputs = newSources.flatMap(_.datasetsIds).distinct)
        val newRead = read.copy(sources = newSources, mainProps = newProps)
        newRead
      }))

    eventualReadsWithLineages map (newReads => {
      val newReadsMap: Map[UUID, Read] = newReads.map(read => read.mainProps.id -> read).toMap

      val linked = lineage.copy(operations = lineage.operations.map(op => newReadsMap.getOrElse(op.mainProps.id, op)))
      new LinkedLineage(linked, lineage)
    })
  }
}

