package com.ms.jedi.dq.predict.rule

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

trait SparkEnv {

  def getSession: SparkSession = {
    val sparkSession = SparkSession.builder
      .appName("DQ-service").master("local").config("spark.driver.memory", "5g")
      .getOrCreate()
    sparkSession
  }

  def getSparkContext: SparkContext = {
    getSession.sparkContext
  }
  
   def getSqlContext: SQLContext = {
    getSession.sqlContext
  }

}