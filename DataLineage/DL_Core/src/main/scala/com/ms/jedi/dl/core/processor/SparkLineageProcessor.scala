/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.processor

import org.slf4s.Logging

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Success
import com.ms.jedi.dl.persistence.api.DataLineageReader
import com.ms.jedi.dl.persistence.api.DataLineageWriter
import com.ms.jedi.dl.common.transformations.AsyncTransformation
import com.ms.jedi.dl.model.DataLineage
import scala.util.Failure

/**
 * The class represents a handler listening on events that Spark triggers when a Dataset is matariealized by an action on a DataFrameWriter.
 *
 * @param persistenceReader     An instance reading data lineages from a persistence layer
 * @param persistenceWriter     An instance writing data lineages to a persistence layer
 * @param lineageTransformation An instance performing modifications on harvested data lineage
 */
class SparkLineageProcessor(
  persistenceReader:     DataLineageReader,
  persistenceWriter:     DataLineageWriter,
  lineageTransformation: AsyncTransformation[DataLineage]) extends Logging {

  import scala.concurrent.ExecutionContext.Implicits._

  def process(rawLineage: DataLineage): Unit = {
    println(s"Processing raw lineage")

    val appId = rawLineage.appId
    val appName = rawLineage.appName
    val attributes = rawLineage.attributes.mkString("<------->")
    val dataTypes = rawLineage.dataTypes.mkString("<------->")
    val operations = rawLineage.operations.mkString("<------->")
    val datasets = rawLineage.datasets.mkString("<------->")

    println(" appId ----------->  " + appId)
    println(" appName ----------->  " + appName)
    println(" attributes ----------->  " + attributes)
    println(" dataTypes ----------->  " + dataTypes)
    println(" operations ----------->  " + operations)
    println(" datasets ----------->  " + datasets)

    val eventuallyStored = for {
      transformedLineage <- lineageTransformation(rawLineage) andThen {
        case Success(_) => println("SparkLineageProcessor----> " + s"lineageTransformation is processed")
        case Failure(t) => println("SparkLineageProcessor----> " + t.printStackTrace())
      }
      storeEvidence <- persistenceWriter.store(transformedLineage) andThen {
        case Success(_) => println("storeEvidence -------Write Done ------> " + s"persistenceWriter is processed")
        case Failure(t) => println("storeEvidence----> " + s"persistenceWriter is Failed" + t.printStackTrace())
      }
    } yield storeEvidence

    println("<----------- Finished ----------------->")

    Await.result(eventuallyStored, 10 minutes)
  }
}
