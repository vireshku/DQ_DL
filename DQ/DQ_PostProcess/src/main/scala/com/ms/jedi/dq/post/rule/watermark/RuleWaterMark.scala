package com.ms.jedi.dq.post.rule.watermark

import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.functions._
import com.ms.jedi.dq.post.rule.model.DQWaterMark
import java.sql.Timestamp
import org.apache.spark.sql.SaveMode
import com.ms.db.util.KeyValParser

trait RuleWaterMark {

  /**
   * Function to update the watermark table
   * @Input
   * sourceTableName     : Value { SourceName_Tablename }
   * lastBatchRunTime   : timestamp of the last batch run
   */
  def updateWaterMark(source: String, lastBatchRunTime: String) = {
    println("updating the water mark")
    val spark = AdlsAdapter.spark(source)
    import spark.sqlContext.implicits._
    val DQAppliedCreatedDate = new Timestamp(System.currentTimeMillis())
    val DQAppliedModifiedDate = new Timestamp(System.currentTimeMillis())
    val data = spark.createDataset(Seq(DQWaterMark.apply(source, lastBatchRunTime, DQAppliedCreatedDate, DQAppliedModifiedDate)))
    data.write.mode(SaveMode.Append).insertInto("ComputeStore.DQPostComputeWaterMark")
  }

  /**
   * Function to retrieve the Previous run's WaterMarkValue which becomes Start Value for the Current Run
   * @Input
   * subjectArea
   * sourceTableName
   * targetTableName
   * @Output
   * waterMarkEndValue : Previous run's WaterMarkValue
   */

  def waterMarkDebut(source: String): String = {
    val spark = AdlsAdapter.spark(source)
    var lstRunCompletedTime = "17530101"
    val lastRunData = spark.table("ComputeStore.DQPostComputeWaterMark").filter(col("source") === source)
    if (lastRunData.take(1).size > 0)
      lstRunCompletedTime = lastRunData.orderBy(col("lastBatchRunTime").desc).first().getString(1)
    println("lstRunCompletedTime is ------------------>" + lstRunCompletedTime)
    lstRunCompletedTime
  }

  /**
   * Function to retrieve upto what time the FileAudit logs are available for the source table to define the WaterMarkEndValue for delta processing
   * @Input
   * waterMarkStartValue : WaterMarkStartValue of the current run
   * @Output             :
   * fileMarkauditDate   : WaterMarkEndValue of the current run
   */

  def waterMarkEnd(source: String, waterMarkStartValue: String): String = {
    println("Inside waterMarkFin refreshed is performed ...................")
    val spark = AdlsAdapter.spark(source)
    val env = KeyValParser.kv().getProperty("env")
    val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("source") === (source)).filter(col("Region").isin(List("Gold", "NonPSOwnedGold"): _*)).select(col("RefreshedTime")).orderBy(col("RefreshedTime").desc)
    if (null != data.take(1) && !data.take(1).isEmpty)
      data.first().getLong(0).toString()
    else
      waterMarkStartValue
  }
}