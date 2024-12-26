/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.listener

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.QueryExecution
import org.apache.spark.sql.util.QueryExecutionListener
import com.ms.jedi.dl.core.initial.SparkLineageInitializer.createEventHandler
import com.ms.jedi.dl.core.harvester.QueryExecutionEventHandler
import com.ms.jedi.dl.core.listener.DLQueryExecutionListener._


class DLQueryExecutionListener(maybeEventHandlerConstructor: => Option[QueryExecutionEventHandler]) extends QueryExecutionListener {

  private lazy val maybeEventHandler: Option[QueryExecutionEventHandler] = maybeEventHandlerConstructor

  /**
    * Listener delegate is lazily evaluated as DalaLineage initialization requires completely initialized SparkSession
    * to be able to use sessionState for duplicate tracking prevention.
    */
  def this() = this(constructEventHandler())

  override def onSuccess(funcName: String, qe: QueryExecution, durationNs: Long): Unit =
      maybeEventHandler.foreach(_.onSuccess(funcName, qe, durationNs))

  override def onFailure(funcName: String, qe: QueryExecution, exception: Exception): Unit =
      maybeEventHandler.foreach(_.onFailure(funcName, qe, exception))

}

object DLQueryExecutionListener {

  private def constructEventHandler(): Option[QueryExecutionEventHandler] = {
    val sparkSession = SparkSession.getActiveSession
      .orElse(SparkSession.getDefaultSession)
      .getOrElse(throw new IllegalStateException("Session is unexpectedly missing. DataLineage cannot be initialized."))
    createEventHandler(sparkSession)
  }

}
