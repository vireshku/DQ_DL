package com.ms.jedi.dq.exec.rule.audit

import org.apache.spark.sql.Dataset
import com.ms.db.util.KeyValParser
import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

trait RuleDeltaTrait {

  def readData(d: String,sourceTableName:String): Dataset[Row] = {
    val spark = AdlsAdapter.spark(sourceTableName)
    if (d.endsWith("tsv"))
      spark.read.format("csv").option("header", "true").option("delimiter", "\t").load(d)
    else
      spark.read.parquet(d)
  }

  def filterData(source:String,sourceTableName: String, waterMarkStartValue: String, waterMarkEndValue: String): Array[String] = {
    val spark = AdlsAdapter.spark(sourceTableName)
    val env = KeyValParser.kv().getProperty("env")
    val adlsPath = KeyValParser.kv().getProperty("aadls").concat("/")
//    val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("Source") === (source)).filter(col("Entity") === (sourceTableName)).filter(col("Region").isin(List("Raw", "NonPSOwnedRaw"): _*))
//      .filter(col("RefreshedTime").>(waterMarkStartValue)).filter(col("RefreshedTime").<=(waterMarkEndValue)).select(col("FilePath")).distinct()
       val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("Source") === (source)).filter(col("Entity") === (sourceTableName)).filter(col("Region").isin(List("RawMerge"): _*))
      .filter(col("RefreshedTime").>(waterMarkStartValue)).filter(col("RefreshedTime").<=(waterMarkEndValue)).select(col("FilePath")).distinct()

    data.collect().map(r => r.getString(0))
  }
}