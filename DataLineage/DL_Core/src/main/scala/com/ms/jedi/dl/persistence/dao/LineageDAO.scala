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

import com.mongodb.casbah.Imports.DBObject
import com.ms.jedi.dl.persistence.api.CloseableIterable
import com.ms.jedi.dl.persistence.api.DataLineageReader.{PageRequest, Timestamp}

import scala.concurrent.{ExecutionContext, Future}

import scala.concurrent.{ExecutionContext, Future}

trait LineageDAO {

  def save(lineage: DBObject)(implicit e: ExecutionContext): Future[Unit]

  def loadByDatasetId(dsId: UUID, overviewOnly: Boolean)(implicit ec: ExecutionContext): Future[Option[DBObject]]

  def searchDataset(path: String, applicationId: String)(implicit ec: ExecutionContext): Future[Option[UUID]]

  def findLatestDatasetIdsByPath(path: String)(implicit ec: ExecutionContext): Future[CloseableIterable[UUID]]

  def findByInputId(datasetId: UUID, overviewOnly: Boolean)(implicit ec: ExecutionContext): Future[CloseableIterable[DBObject]]

  def findDatasetDescriptors(maybeText: Option[String], pageRequest: PageRequest)
                            (implicit ec: ExecutionContext): Future[CloseableIterable[DBObject]]

  def getDatasetDescriptor(id: UUID)(implicit ec: ExecutionContext): Future[DBObject]
}

trait VersionedLineageDAO extends VersionedDAO {

  def save(lineage: DBObject)(implicit e: ExecutionContext): Future[Unit]

  def loadByDatasetId(dsId: UUID, overviewOnly: Boolean)(implicit ec: ExecutionContext): Future[Option[DBObject]]

  def searchDataset(path: String, applicationId: String)(implicit ec: ExecutionContext): Future[Option[UUID]]

  def getLastOverwriteTimestampIfExists(path: String)(implicit ec: ExecutionContext): Future[Option[Timestamp]]

  def findDatasetIdsByPathSince(path: String, since: Timestamp)(implicit ec: ExecutionContext): Future[CloseableIterable[UUID]]

  def findByInputId(datasetId: UUID, overviewOnly: Boolean)(implicit ec: ExecutionContext): Future[CloseableIterable[DBObject]]

  def findDatasetDescriptors(maybeText: Option[String], pageRequest: PageRequest)
                            (implicit ec: ExecutionContext): Future[CloseableIterable[DBObject]]

  def getDatasetDescriptor(id: UUID)(implicit ec: ExecutionContext): Future[Option[DBObject]]

  def countDatasetDescriptors(maybeText: Option[String], asAtTime: Timestamp)(implicit ec: ExecutionContext): Future[Int]
}