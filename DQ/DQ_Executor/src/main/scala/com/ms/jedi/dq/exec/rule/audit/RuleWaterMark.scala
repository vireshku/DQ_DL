package com.ms.jedi.dq.exec.rule.audit

import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.functions._
import com.ms.db.util.KeyValParser

trait RuleWaterMark {

  /**
   * Function to update the watermark table
   * @Input
   * sourceTableName     : Value { SourceName_Tablename Ex. DimRevSumHierarchy }
   * waterMarkStartValue : WaterMarkStartValue of the current run
   * waterMarkEndValue   : WaterMarkEndValue of the current run
   */
  def updateWaterMark(source: String, entity: String, waterMarkEndVal: String) = {
    println("updating the water mark")
    val ss = AdlsAdapter.spark(entity)
    val q = s"insert into ComputeStore.DQComputeWaterMark values('" + source + "', '" + entity + "' ,'17530101', " + waterMarkEndVal + " , " + null + " , " + null + ")"
    ss.sql(q)
  }

  /**
   * Function to retrieve the Previous run's WaterMarkValue which becomes Start Value for the Current Run
   * @Input
   * subjectArea     : Value { OMIDimensions from EnvConfig.conf }
   * sourceTableName : Value { SourceName_Tablename Ex. SMDP_DimRevSumHierarchy }
   * targetTableName : Dim/Fact Table for processing { DimRevSumHierarchy }
   * @Output
   * waterMarkEndValue : Previous run's WaterMarkValue
   */

  def waterMarkDebut(source: String, entity: String): String = {
    val spark = AdlsAdapter.spark(entity)
    var lstRunCompletedTime = "17530101"
    val lastRunData = spark.table("ComputeStore.DQComputeWaterMark").filter(col("SourceEntityName") === entity).filter(col("SubjectArea") === source).select(col("WaterMarkEndValue"))
    if (!lastRunData.take(1).isEmpty)
      lstRunCompletedTime = lastRunData.orderBy(col("WaterMarkEndValue").desc).first().getString(0)
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

  def waterMarkEnd(source: String, sourceTableName: String, waterMarkStartValue: String): String = {
    println("Inside waterMarkFin refreshed is performed ...................")
    val spark = AdlsAdapter.spark(sourceTableName)
    val env = KeyValParser.kv().getProperty("env")
    //val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("Source") === (source)).filter(col("Entity") === (sourceTableName)).filter(col("Region").isin(List("Raw", "NonPSOwnedRaw"): _*)).select(col("RefreshedTime")).orderBy(col("RefreshedTime").desc)
    val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("Source") === (source)).filter(col("Entity") === (sourceTableName)).filter(col("Region").isin(List("RawMerge"): _*)).select(col("RefreshedTime")).orderBy(col("RefreshedTime").desc)
    if (data.take(1).isEmpty)
      waterMarkStartValue
    else
      data.first().getLong(0).toString()
  }
}