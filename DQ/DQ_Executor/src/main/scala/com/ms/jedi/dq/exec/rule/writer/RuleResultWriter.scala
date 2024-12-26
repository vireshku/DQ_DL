package com.ms.jedi.dq.exec.rule.writer

/**
 * Write class for producing qualified data in to the lake
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SaveMode
import com.ms.db.adapter.AdlsAdapter
import net.liftweb.json.DefaultFormats
import org.apache.spark.sql.functions._
import com.ms.db.util.KeyValParser
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.text.SimpleDateFormat

object RuleResultWriter {

  def write(path: String, data: Dataset[Row]) = {
    System.setProperty("hadoop.home.dir", "c:\\spark");
    println("starting to write to the lake....")
    val goldPath = path.replace("RawMerge", "Gold")
    val adls = KeyValParser.kv().getProperty("aadls")
    println("goldPath --> " + goldPath)
    data.coalesce(1).write.mode(SaveMode.Overwrite).parquet(goldPath + ".tmp/")
    try {
      val oo = dbutils.fs.ls(goldPath + ".tmp/")
      val partitionPath = dbutils.fs.ls(goldPath + ".tmp/").filter(file => file.name.endsWith(".parquet"))(0).path
      println("write partitionPath --> " + partitionPath)
      val currDateTime = time()
      dbutils.fs.cp(partitionPath, goldPath.substring(0, goldPath.length() - 8) + "_" + currDateTime + ".parquet")
      dbutils.fs.rm(goldPath + ".tmp/", recurse = true)
    } catch {
      case e: FileNotFoundException => {
        println("ERROR : write : Missing file exception : " + e.getMessage)
      }
      case e: IOException => {
        println("ERROR : write : IO Exception : " + e.getMessage)
      }
      case e: Exception => {
        println("ERROR : write : Unknown Exception : " + e.getMessage)
      }
    }
    println("write to the lake completed....")
  }

  def writeTSV(path: String, data: Dataset[Row]) = {
    println("starting to write to the lake as a TSV....")
    val goldPath = path.replace("NonPSOwnedRaw", "NonPSOwnedGold")
    data.repartition(1).write
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("sep", "\t")
      .mode("overwrite")
      .save(goldPath)
    println("write to the lake as a TSV completed....")
  }

  def writeSingleTSV(path: String, data: Dataset[Row]) = {
    println("starting to write to the lake as a TSV....")
    val goldPath = path.replace("NonPSOwnedRaw", "NonPSOwnedGold")
    data.repartition(1).write
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("sep", "\t")
      .mode("overwrite")
      .save(goldPath + ".tmp")

    try {
      val oo = dbutils.fs.ls(goldPath + ".tmp/")
      val partitionPath = dbutils.fs.ls(goldPath + ".tmp/").filter(file => file.name.endsWith(".csv"))(0).path
      println("partitionPath --> " + partitionPath)
      val currDateTime = time()
      dbutils.fs.cp(partitionPath, goldPath.substring(0, goldPath.length() - 4) + "_" + currDateTime + ".tsv")
      dbutils.fs.rm(goldPath + ".tmp/", recurse = true)
    } catch {
      case e: FileNotFoundException => {
        println("ERROR : writeDfToCSVSingleFile : Missing file exception : " + e.getMessage)
      }
      case e: IOException => {
        println("ERROR : writeDfToCSVSingleFile : IO Exception : " + e.getMessage)
      }
      case e: Exception => {
        println("ERROR : writeDfToCSVSingleFile : Unknown Exception : " + e.getMessage)
      }
    }
    println("write to the lake as a TSV completed....")
  }

  def time(): String = {
    val now = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("yyyyMMdd_mmss")
    minuteFormat.format(now)
  }
}