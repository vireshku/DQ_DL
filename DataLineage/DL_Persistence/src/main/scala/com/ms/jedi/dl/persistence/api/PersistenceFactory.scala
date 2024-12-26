/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.persistence.api

import org.apache.commons.configuration.Configuration
import org.slf4s.Logging

/**
  * The abstract class represents a factory of persistence readers and writers for all main data lineage entities.
  *
  * @param configuration A source of settings
  */
abstract class PersistenceFactory(protected val configuration: Configuration) extends Logging {

  /**
    * The method creates a writer to the persistence layer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity.
    *
    * @return A writer to the persistence layer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity
    */
  def createDataLineageWriter: DataLineageWriter

  /**
    * The method creates a reader from the persistence layer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity.
    *
    * @return An optional reader from the persistence layer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity
    */
  def createDataLineageReader: Option[DataLineageReader]
}
