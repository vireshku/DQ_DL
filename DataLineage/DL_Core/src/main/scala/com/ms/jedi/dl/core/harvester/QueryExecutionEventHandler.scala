/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */
package com.ms.jedi.dl.core.harvester

import scala.language.postfixOps
import org.slf4s.Logging
import org.apache.spark.sql.execution.QueryExecution
import com.ms.jedi.dl.core.processor.SparkLineageProcessor

class QueryExecutionEventHandler(harvesterFactory: DataLineageBuilderFactory, lineageProcessor: SparkLineageProcessor)
  extends Logging {

  /**
   * The method is executed when an action execution is successful.
   *
   * @param funcName   A name of the executed action.
   * @param qe         A Spark object holding lineage information (logical, optimized, physical plan)
   * @param durationNs Duration of the action execution [nanoseconds]
   */
  def onSuccess(funcName: String, qe: QueryExecution, durationNs: Long): Unit = {
    println(s"Action '$funcName' execution succeeded")

    if (funcName == "save" || funcName == "saveAsTable" || funcName == "insertInto") {
      println(s"Start tracking lineage for action '$funcName'")

      val maybeLineage =
        harvesterFactory.
          createBuilder(qe.analyzed, Some(qe.executedPlan), qe.sparkSession.sparkContext).
          buildLineage()

      maybeLineage match {
        case None => println(s"The write result was ignored. Skipping lineage.")
        case Some(lineage) =>
          {
            println("QueryExecutionEventHandler ------------->>  +  going to lineageProcessor---")
            lineageProcessor.process(lineage)
          }
      }

      println(s"Lineage tracking applied for action '$funcName' is done.")
    } else {
      println(s"Skipping lineage tracking for action '$funcName'")
    }
  }

  /**
   * The method is executed when an error occurs during an action execution.
   *
   * @param funcName  A name of the executed action.
   * @param qe        A Spark object holding lineage information (logical, optimized, physical plan)
   * @param exception An exception describing the reason of the error
   */
  def onFailure(funcName: String, qe: QueryExecution, exception: Exception): Unit = {
    //log.error
    println(s"Action '$funcName' execution failed", exception)
  }
}
