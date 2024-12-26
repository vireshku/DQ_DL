/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.persistence.impl

import org.apache.commons.configuration.Configuration
import com.ms.jedi.dl.persistence.api.PersistenceFactory
import com.ms.jedi.dl.persistence.api.DataLineageWriter
import com.ms.jedi.dl.persistence.api.DataLineageReader
import com.ms.jedi.dl.persistence.dao.MultiVersionLineageDAO
import com.ms.jedi.dl.persistence.dao.LineageDAOv3
import com.ms.jedi.dl.persistence.dao.LineageDAOv4
import com.ms.jedi.dl.persistence.dao.LineageDAOv3

/**
  * The object contains static information about settings needed for initialization of the MongoPersistenceWriterFactory class.
  */
object MongoPersistenceFactory {
  val mongoDbUrlKey = "spline.mongodb.url"
  val mongoDbNameKey = "spline.mongodb.name"
}

/**
  * The class represents a factory creating Mongo persistence writers for all main data lineage entities.
  *
  * @param configuration A source of settings
  */
class MongoPersistenceFactory(configuration: Configuration) extends PersistenceFactory(configuration) {

  import MongoPersistenceFactory._
  import com.ms.jedi.dl.common.ConfigurationImplicits._

  private val mongoConnection = new MongoConnectionImpl(
    dbUrl = configuration getRequiredString mongoDbUrlKey,
    dbName = configuration getRequiredString mongoDbNameKey)

  //private val dao = new MultiVersionLineageDAO(new LineageDAOv3(mongoConnection), new LineageDAOv4(mongoConnection))
private val dao = null
  /**
    * The method creates a persistence writer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity.
    *
    * @return A persistence writer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity
    */
  override def createDataLineageWriter: DataLineageWriter = new MongoDataLineageWriter(dao)

  /**
    * The method creates a reader from the persistence layer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity.
    *
    * @return An optional reader from the persistence layer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity
    */
  override def createDataLineageReader: Option[DataLineageReader] = Some(new MongoDataLineageReader(dao))
}