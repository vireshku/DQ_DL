/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.persistence.impl


import org.slf4s.Logging
import salat.grater

import scala.concurrent.{ExecutionContext, Future}
import com.ms.jedi.dl.persistence.api.DataLineageWriter
import com.ms.jedi.dl.model.DataLineage
import com.ms.jedi.dl.persistence.dao.LineageDAO
import com.ms.jedi.dl.persistence.serialise.BSONSalatContext._

class MongoDataLineageWriter(lineageDAO: LineageDAO) extends DataLineageWriter with Logging {
  override def store(lineage: DataLineage)(implicit ec: ExecutionContext): Future[Unit] = {
    log debug s"Storing lineage objects"
    lineageDAO.save(grater[DataLineage].asDBObject(lineage))
  }
}
