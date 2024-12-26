package com.ms.jedi.dq.merge.util

/**
 * DQMergeUtil is the trait to wrap utility api(s) related to the DQMerge Functionality
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.db.util.KeyValParser
import java.sql.Time
import java.util.Calendar
import java.text.SimpleDateFormat
import com.ms.jedi.dq.merge.watermark.DQMergeWaterMark

trait DQMergeUtil extends DQMergeWaterMark {

  final val ALL = "Full"
  final val INCR = "Delta"

  def time(): String = {
    val now = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("yyyyMMdd_mmss")
    minuteFormat.format(now)
  }

  def getDistinctSourceEntity(): Dataset[Row] = {
    val spark = AdlsAdapter.spark("Merge")
    val env = KeyValParser.kv().getProperty("env")
    val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("Region") === "Raw").select(col("source"), col("entity")).distinct()
    data
  }

  def getDistinctSourceEntity(source: String): Dataset[Row] = {
    val spark = AdlsAdapter.spark("Merge")
    val env = KeyValParser.kv().getProperty("env")
    val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("Source") === source).filter(col("Region") === "Raw").select(col("entity"), col("FullDelta")).distinct()
    data
  }

  def getBatchList(source: String, entity: String, fullFlag: String): (Array[String], String) = {
    val waterMarkStartValue = waterMarkStart(source, entity, fullFlag)
    val waterMarkEndValue = waterMarkEnd(source, entity, fullFlag)
    val spark = AdlsAdapter.spark("Merge")
    val env = KeyValParser.kv().getProperty("env")
    val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("Source") === (source)).filter(col("Entity") === (entity)).filter(col("Region").isin(List("Raw"): _*)).filter(col("FullDelta") === (fullFlag))
      .filter(col("RefreshedTime").>(waterMarkStartValue)).filter(col("RefreshedTime").<=(waterMarkEndValue)).select(col("FilePath")).distinct()
    (data.collect().map(r => r.getString(0)), waterMarkEndValue)
  }

}