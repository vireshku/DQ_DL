package com.ms.jedi.dq.merge.process.support

/**
 * DQMergeProcSupport is the trait to wrap key process api(s) related to the DQMerge Functionality, This is the action class for the DQMergeProcessor
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.jedi.dq.merge.watermark.DQMergeWaterMark
import org.apache.spark.sql.SparkSession
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.ms.db.util.KeyValParser
import com.ms.jedi.dq.merge.util.DQMergeUtil
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SaveMode
import com.ms.db.adapter.AdlsAdapter

trait DQMergeProcSupport extends DQMergeUtil {

  def mergeFull(source: String, entity: String, potentialLakePaths: Array[String]) = {

    val adlsPath = KeyValParser.kv().getProperty("aadls").concat("/")
    val env = KeyValParser.kv().getProperty("env")
    val spark = AdlsAdapter.spark("Merge")
    val tPath = adlsPath.concat(env).concat("/").concat("RawMerge").concat("/").concat(source).concat("/").concat(entity).concat("/").concat("Full").concat("/").concat("M_F_")
      .concat(entity + "_").concat(time()).concat(".parquet")
    println("Inside full potentialLakePaths --------------> source and entity is ---->  " + source + "   <--------->  " + entity)
    println("target Path ----------->   " + tPath)
    val mergerdData = spark.read.option("mergeSchema", "true").parquet(potentialLakePaths: _*)
    val fData = mergerdData.withColumn("filename", input_file_name())
    fData.coalesce(1).write.mode(SaveMode.Overwrite).parquet(tPath + "temp")
    val partitionPath = dbutils.fs.ls(tPath + "temp").filter(file => file.name.endsWith("." + "parquet"))(0).path
    dbutils.fs.cp(partitionPath, tPath)
    dbutils.fs.rm(tPath + "temp", recurse = true)
  }

  def mergeDelta(source: String, entity: String, potentialLakePaths: Array[String]) = {

    val adlsPath = KeyValParser.kv().getProperty("aadls").concat("/")
    val env = KeyValParser.kv().getProperty("env")
    val spark = AdlsAdapter.spark("Merge")
    println("Inside Delta potentialLakePaths1 --------------> source and entity is ---->  " + source + "   <--------->  " + entity)
    val tPath1 = adlsPath.concat(env).concat("/").concat("RawMerge").concat("/").concat(source).concat("/").concat(entity).concat("/").concat("Delta").concat("/").concat("M_D_")
      .concat(entity + "_").concat(time()).concat(".parquet")
    println("target Path1 ----------->   " + tPath1)
    val mergerdData1 = spark.read.option("mergeSchema", "true").parquet(potentialLakePaths: _*)
    val fData1 = mergerdData1.withColumn("filename", input_file_name())
    fData1.coalesce(1).write.mode(SaveMode.Overwrite).parquet(tPath1 + "temp")
    val partitionPath1 = dbutils.fs.ls(tPath1 + "temp").filter(file => file.name.endsWith("." + "parquet"))(0).path
    dbutils.fs.cp(partitionPath1, tPath1)
    dbutils.fs.rm(tPath1 + "temp", recurse = true)
  }

  def merge(source: String, entity: String, potentialLakePaths: Array[String], fullDelta: String) = {

    val adlsPath = KeyValParser.kv().getProperty("aadls").concat("/")
    val env = KeyValParser.kv().getProperty("env")
    val spark = AdlsAdapter.spark("Merge")
    val tPath = adlsPath.concat(env).concat("/").concat("RawMerge").concat("/").concat(source).concat("/").concat(entity).concat("/").concat(fullDelta).concat("/").concat("M_" + fullDelta.charAt(0) + "_")
      .concat(entity + "_").concat(time()).concat(".parquet")
    println("Inside " + fullDelta + " potentialLakePaths --------------> source and entity is ---->  " + source + "   <--------->  " + entity)
    println("target Path ----------->   " + tPath)
    val mergerdData = spark.read.option("mergeSchema", "true").parquet(potentialLakePaths: _*)
    val fData = mergerdData.withColumn("filename", input_file_name())
    fData.coalesce(1).write.mode(SaveMode.Overwrite).parquet(tPath + "temp")
    val partitionPath = dbutils.fs.ls(tPath + "temp").filter(file => file.name.endsWith("." + "parquet"))(0).path
    dbutils.fs.cp(partitionPath, tPath)
    dbutils.fs.rm(tPath + "temp", recurse = true)
  }
}