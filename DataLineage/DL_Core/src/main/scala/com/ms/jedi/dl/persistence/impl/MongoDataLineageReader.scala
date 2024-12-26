/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.persistence.impl

import java.util.UUID
import _root_.salat._
import com.mongodb.casbah.Imports._
import com.ms.jedi.dl.model._
import scala.concurrent.{ExecutionContext, Future}
import com.ms.jedi.dl.persistence.api.DataLineageReader
import com.ms.jedi.dl.persistence.api.CloseableIterable
import com.ms.jedi.dl.persistence.api.DataLineageReader.PageRequest
import com.ms.jedi.dl.persistence.dao.LineageDAO



class MongoDataLineageReader(lineageDAO: LineageDAO) extends DataLineageReader {

  import com.ms.jedi.dl.persistence.serialise.BSONSalatContext._

  /**
    * The method loads a particular data lineage from the persistence layer.
    *
    * @param dsId An unique identifier of a data lineage
    * @return A data lineage instance when there is a data lineage with a given id in the persistence layer, otherwise None
    */
  override def loadByDatasetId(dsId: UUID, overviewOnly: Boolean)(implicit ec: ExecutionContext): Future[Option[DataLineage]] = {
    println("MongoDataLineageReader  loadByDatasetId -----------------> start")
    lineageDAO.loadByDatasetId(dsId, overviewOnly).map(_.map(grater[DataLineage].asObject(_)))
  }

  /**
    * The method scans the persistence layer and tries to find a dataset ID for a given path and application ID.
    *
    * @param path          A path for which a dataset ID is looked for
    * @param applicationId An application for which a dataset ID is looked for
    * @return An identifier of a meta data set
    */
  override def searchDataset(path: String, applicationId: String)(implicit ec: ExecutionContext): Future[Option[UUID]] =
    lineageDAO.searchDataset(path, applicationId)

  /**
    * The method loads the latest data lineage from the persistence for a given path.
    *
    * @param path A path for which a lineage graph is looked for
    * @return The latest data lineage
    */
  override def findLatestDatasetIdsByPath(path: String)(implicit ec: ExecutionContext): Future[CloseableIterable[UUID]] =
    lineageDAO.findLatestDatasetIdsByPath(path)

  /**
    * The method loads composite operations for an input datasetId.
    *
    * @param datasetId A dataset ID for which the operation is looked for
    * @return Composite operations with dependencies satisfying the criteria
    */
  override def findByInputId(datasetId: UUID, overviewOnly: Boolean)(implicit ec: ExecutionContext): Future[CloseableIterable[DataLineage]] =
    lineageDAO.findByInputId(datasetId, overviewOnly: Boolean).map(_.map(grater[DataLineage].asObject(_)))

  /**
    * The method gets all data lineages stored in persistence layer.
    *
    * @return Descriptors of all data lineages
    */
  override def findDatasets(maybeText: Option[String], pageRequest: PageRequest)
                           (implicit ec: ExecutionContext): Future[CloseableIterable[PersistedDatasetDescriptor]] =
    lineageDAO.findDatasetDescriptors(maybeText, pageRequest).map(_.map(bObject =>
      grater[PersistedDatasetDescriptor].asObject(bObject)))

  /**
    * The method returns a dataset descriptor by its ID.
    *
    * @param id An unique identifier of a dataset
    * @return Descriptors of all data lineages
    */
  override def getDatasetDescriptor(id: UUID)(implicit ec: ExecutionContext): Future[PersistedDatasetDescriptor] =
    lineageDAO.getDatasetDescriptor(id).map(grater[PersistedDatasetDescriptor].asObject(_))
}
