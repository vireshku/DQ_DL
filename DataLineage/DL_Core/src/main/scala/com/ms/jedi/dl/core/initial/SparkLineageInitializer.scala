/**
 * Entry point for Lineage Initializer
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.initial

import org.apache.commons.configuration._
import org.apache.spark
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.internal.StaticSQLConf.QUERY_EXECUTION_LISTENERS
import org.slf4s.Logging
import com.ms.jedi.dl.core.conf.DLConfigurer.DLMode._
import com.ms.jedi.dl.core.conf._
import com.ms.jedi.dl.core.listener.DLQueryExecutionListener
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal
import com.ms.jedi.dl.core.harvester.QueryExecutionEventHandler
import com.ms.jedi.dl.core.conf.DLConfigurer
import org.apache.hadoop.security.UserGroupInformation.HadoopConfiguration
import com.ms.jedi.dl.coreadapterapi.SparkVersionRequirement


/**
 * The object contains logic needed for initialization of the library
 */
object SparkLineageInitializer extends Logging with App{

  def enableLineageTracking(sparkSession: SparkSession): SparkSession =
    SparkSessionWrapper(sparkSession).enableLineageTracking()

  def enableLineageTracking(sparkSession: SparkSession, configurer: DLConfigurer): SparkSession =
    SparkSessionWrapper(sparkSession).enableLineageTracking(configurer)

  def createEventHandler(sparkSession: SparkSession): Option[QueryExecutionEventHandler] =
    SparkSessionWrapper(sparkSession).createEventHandler()

  /**
   * The class is a wrapper around Spark session and performs all necessary registrations and procedures for initialization of the library.
   *
   * @param sparkSession A Spark session
   */
  implicit class SparkSessionWrapper(sparkSession: SparkSession) {

    private implicit val executionContext: ExecutionContext = ExecutionContext.global
    private def defaultSplineConfigurer = new DefaultDLConfigurer(defaultSplineConfiguration, sparkSession)

    /**
     * The method performs all necessary registrations and procedures for initialization of the library.
     *
     * @param configurer A collection of settings for the library initialization
     * @return An original Spark session
     */
    def enableLineageTracking(configurer: DLConfigurer = defaultSplineConfigurer): SparkSession = {
      val splineConfiguredForCodelessInit = sparkSession.sparkContext.getConf
        .getOption(QUERY_EXECUTION_LISTENERS.key).toSeq
        .flatMap(s => s.split(",").toSeq)
        .contains(classOf[DLQueryExecutionListener].getCanonicalName)
      if (!splineConfiguredForCodelessInit || spark.SPARK_VERSION.startsWith("2.2")) {
        if (splineConfiguredForCodelessInit) {
          log.warn("""
            |Spline lineage tracking is also configured for codeless initialization, but codeless init is
            |supported on Spark 2.3+ and not current version 2.2. Spline will be initialized only via code call to
            |enableLineageTracking i.e. the same way as is now."""
            .stripMargin.replaceAll("\n", " "))
        }
        // This is the place where registering the query execution listener
        createEventHandler(configurer)
          .foreach(h => sparkSession.listenerManager.register(new DLQueryExecutionListener(Some(h))))
      } else {
        log.warn("""
          |Spline lineage tracking is also configured for codeless initialization.
          |It wont be initialized by this code call to enableLineageTracking now."""
          .stripMargin.replaceAll("\n", " "))
      }
      sparkSession
    }

    def createEventHandler(): Option[QueryExecutionEventHandler] = {
      val configurer = new DefaultDLConfigurer(defaultSplineConfiguration, sparkSession)
      if (configurer.dlMode != DISABLED) {
        createEventHandler(configurer)
      } else {
        None
      }
    }

    private def createEventHandler(configurer: DLConfigurer): Option[QueryExecutionEventHandler] = {
      if (configurer.dlMode != DISABLED) {
        if (!getOrSetIsInitialized()) {
          println(s"DataLineage v${DLBuildInfo.version} is initializing...")
          try {
        //    SparkVersionRequirement.instance.requireSupportedVersion()
            val eventHandler = configurer.queryExecutionEventHandler
            println(s"DataLineage successfully initialized. Spark Lineage tracking is ENABLED.")
            Some(eventHandler)
          } catch {
            case NonFatal(e) if configurer.dlMode == BEST_EFFORT =>
              log.error(s"DataLineage initialization failed! Spark Lineage tracking is DISABLED.", e)
              None
          }
        } else {
          println("DataLineage lineage tracking is already initialized!")
          None
        }
      } else {
        None
      }
    }

    private[core] val defaultSplineConfiguration = {
      val splinePropertiesFileName = "spline.properties"

      val systemConfOpt = Some(new SystemConfiguration)
      val propFileConfOpt = Try(new PropertiesConfiguration(splinePropertiesFileName)).toOption
      val hadoopConfOpt = Some(new HadoopConfiguration(sparkSession.sparkContext.hadoopConfiguration))
      val sparkConfOpt = Some(new SparkConfiguration(sparkSession.sparkContext.getConf))

      new CompositeConfiguration(Seq(
        hadoopConfOpt,
        sparkConfOpt,
        systemConfOpt,
        propFileConfOpt).flatten.asJava)
    }

    private def getOrSetIsInitialized(): Boolean = sparkSession.synchronized {
      val sessionConf = sparkSession.conf
      sessionConf getOption "spline.initialized_flag" match {
        case Some(_) =>
          true
        case None =>
          sessionConf.set("spline.initialized_flag", true.toString)
          false
      }
    }

  }

  val initFlagKey = "spline.initialized_flag"
}
