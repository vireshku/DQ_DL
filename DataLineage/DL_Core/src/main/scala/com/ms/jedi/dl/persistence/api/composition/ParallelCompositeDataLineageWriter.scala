/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.persistence.api.composition

import org.slf4s.Logging


import scala.concurrent.{ExecutionContext, Future}
import com.ms.jedi.dl.persistence.api.DataLineageWriter
import com.ms.jedi.dl.model.DataLineage

/**
  * The class represents a parallel composite writer to various persistence layers for the [[com.ms.jedi.dl.model.DataLineage DataLineage]] entity.
  *
  * @param writers a set of internal writers specific to particular  persistence layers
  */
class ParallelCompositeDataLineageWriter(writers: Seq[DataLineageWriter]) extends DataLineageWriter with Logging {

  /**
    * The method stores a particular data lineage to the underlying persistence layers.
    *
    * @param lineage A data lineage that will be stored
    */
  override def store(lineage: DataLineage)(implicit ec: ExecutionContext): Future[Unit] = {
    log debug s"Calling underlying writers (${writers.length})"
    val futures = for (w <- writers) yield w.store(lineage)
    Future.sequence(futures).map(_ => Unit)
  }

}
