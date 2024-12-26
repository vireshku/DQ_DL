/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.conf

import org.apache.commons.configuration.Configuration
import org.apache.spark.sql.SparkSession
import org.slf4s.Logging
import com.ms.jedi.dl.core.harvester.{ DataLineageBuilderFactory, QueryExecutionEventHandler }
import scala.concurrent.ExecutionContext
import com.ms.jedi.dl.core.processor.SparkLineageProcessor
import com.ms.jedi.dl.core.conf.DLConfigurer.DLMode.DLMode
import com.ms.jedi.dl.persistence.api.PersistenceFactory
import com.ms.jedi.dl.persistence.api.DataLineageReader
import com.ms.jedi.dl.common.transformations.AsyncTransformationPipeline
import com.ms.jedi.dl.transformation.LineageProjectionMerger
import com.ms.jedi.dl.transformation.DataLineageLinker
import com.ms.jedi.dl.core.conf.DLConfigurer.DLMode
import com.ms.jedi.dl.core.conf.DLConfigurer.DLMode._
import com.ms.jedi.dl.persistence.api.PersistenceFactory
import com.ms.jedi.dl.persistence.api.NopDataLineageReader
import com.ms.jedi.dl.persistence.api.DataLineageReader

/**
 * The object contains static information about default settings needed for initialization of the library.
 */
object DefaultDLConfigurer {

  //noinspection TypeAnnotation
  object ConfProperty {
    val PERSISTENCE_FACTORY = "vk.dl.persistence.factory"

    /**
     * How DL should behave.
     *
     * @see [[DLMode]]
     */
    val MODE = "vk.dl.mode"
    val MODE_DEFAULT = BEST_EFFORT.toString
  }

}

/**
 * The class represents default settings needed for initialization of the library.
 *
 * @param configuration A source of settings
 */
class DefaultDLConfigurer(configuration: Configuration, sparkSession: SparkSession) extends DLConfigurer with Logging {

  import DefaultDLConfigurer.ConfProperty._
  import com.ms.jedi.dl.common.ConfigurationImplicits._

  private implicit val executionContext: ExecutionContext = ExecutionContext.global

  lazy val dlMode: DLMode = {
    val modeName = configuration.getString(MODE, MODE_DEFAULT)
    try DLMode withName modeName
    catch {
      case _: NoSuchElementException => throw new IllegalArgumentException(
        s"Invalid value for property $MODE=$modeName. Should be one of: ${DLMode.values mkString ", "}")
    }
  }

  lazy val queryExecutionEventHandler: QueryExecutionEventHandler =
    new QueryExecutionEventHandler(lineageHarvester, lineageProcessor)

  private lazy val lineageHarvester = new DataLineageBuilderFactory(sparkSession.sparkContext.hadoopConfiguration)

  private lazy val lineageProcessor = {
    val lineageReader = persistenceFactory.createDataLineageReader getOrElse NopDataLineageReader
    val lineageWriter = persistenceFactory.createDataLineageWriter
    new SparkLineageProcessor(lineageReader, lineageWriter, lineageTransformationPipeline(lineageReader))
  }

  private lazy val persistenceFactory: PersistenceFactory = {
    // val persistenceFactoryClassName = configuration getRequiredString PERSISTENCE_FACTORY
    val persistenceFactoryClassName = "com.ms.jedi.dl.persistence.impl.MongoPersistenceFactory"

    log debug s"Instantiating persistence factory: $persistenceFactoryClassName"

    Class.forName(persistenceFactoryClassName)
      .getConstructor(classOf[Configuration])
      .newInstance(configuration)
      .asInstanceOf[PersistenceFactory]
  }

  private def lineageTransformationPipeline(lineageReader: DataLineageReader) = {
    println("lineageTransformationPipeline----------------------------->")
    new AsyncTransformationPipeline(
      LineageProjectionMerger,
      new DataLineageLinker(lineageReader))
  }

}
