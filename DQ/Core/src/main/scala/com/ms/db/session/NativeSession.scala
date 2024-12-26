/**
 * Spark session scala object ,based on factory pattern
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */
package com.ms.db.session

import org.apache.spark.sql.SparkSession

object NativeSession {

  def localspark: SparkSession = {
    val sparkSession = SparkSession.builder.config("spark.driver.memory", "5g").appName("OMI-Databricks-Spark").master("local").getOrCreate()
    sparkSession
  }

  def spark(entity: String): SparkSession = {
    val sparkSession = SparkSession.builder.appName("DQ-Databricks-Spark-" + entity).enableHiveSupport().getOrCreate()
    sparkSession
  }

}