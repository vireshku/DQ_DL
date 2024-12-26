/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

 
package com.ms.jedi.dl.persistence.api.composition

import org.apache.commons.configuration.Configuration
import com.ms.jedi.dl.persistence.api.{DataLineageReader, DataLineageWriter, PersistenceFactory}


object ParallelCompositeFactory {
  val factoriesKey = "spline.persistence.composition.factories"
}

/**
  * The class represents a parallel composition of various persistence writers.
  *
  * @param configuration A source of settings
  */
class ParallelCompositeFactory(configuration: Configuration) extends PersistenceFactory(configuration) {

  import ParallelCompositeFactory._
  import com.ms.jedi.dl.common.ConfigurationImplicits._

  private val factories =
    configuration
      .getRequiredStringArray(factoriesKey)
      .map(className => {
        log debug s"Instantiating underlying factory: $className"
        Class.forName(className.trim)
          .getConstructor(classOf[Configuration])
          .newInstance(configuration)
          .asInstanceOf[PersistenceFactory]
      })

  /**
    * The method creates a parallel composite writer to various persistence layers for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity.
    *
    * @return A parallel composite writer to various persistence layers for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity
    */
  override def createDataLineageWriter: DataLineageWriter =
    new ParallelCompositeDataLineageWriter(factories.map(factory => {
      log debug s"${factory.getClass.getName}: create writer"
      factory.createDataLineageWriter
    }))

  /**
    * The method creates a reader from the persistence layer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity.
    *
    * @return An optional reader from the persistence layer for the [[za.co.absa.spline.model.DataLineage DataLineage]] entity
    */
  override def createDataLineageReader: Option[DataLineageReader] = {
    val readers = factories.flatMap(_.createDataLineageReader)
    if (readers.isEmpty) None
    else Some(new ParallelCompositeDataLineageReader(readers))
  }
}
