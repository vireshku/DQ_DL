/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.persistence.api

import scala.concurrent.{ExecutionContext, Future}
import com.ms.jedi.dl.model.DataLineage

/**
  * The trait represents a writer to a persistence layer for the [[com.ms.jedi.dl.model.DataLineage DataLineage]] entity.
  */
trait DataLineageWriter {

  /**
    * The method stores a particular data lineage to the persistence layer.
    *
    * @param lineage A data lineage that will be stored
    */
  def store(lineage: DataLineage)(implicit ec: ExecutionContext) : Future[Unit]
}
