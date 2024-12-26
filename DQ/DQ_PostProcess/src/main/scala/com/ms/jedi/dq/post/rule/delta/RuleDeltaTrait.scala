package com.ms.jedi.dq.post.rule.delta

import org.apache.spark.sql.Dataset
import com.ms.db.util.KeyValParser
import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import com.ms.jedi.dq.post.rule.model.DQRuleAudit
import org.apache.spark.sql.Dataset
import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import scala.collection.mutable.Map
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.catalyst.encoders.RowEncoder

trait RuleDeltaTrait {

  def filterData(source: String, waterMarkStartValue: String, waterMarkEndValue: String): Map[DQPostMetadata, Dataset[Row]] = {
    val spark = AdlsAdapter.spark(source)
    val env = KeyValParser.kv().getProperty("env")
    val adlsPath = KeyValParser.kv().getProperty("aadls").concat("/")
    val deltaBatchMap = scala.collection.mutable.Map[DQPostMetadata, Dataset[Row]]()
    val data = spark.table("ComputeStore.LakeObserver" + env).filter(col("Source") === (source)).filter(col("Region").isin(List("Gold", "NonPSOwnedGold"): _*))
      .filter(col("RefreshedTime").>(waterMarkStartValue)).filter(col("RefreshedTime").<=(waterMarkEndValue)).select(col("FilePath"), col("Entity")).distinct()
    import spark.implicits._
    data.collect().map({ r =>
      val d = DQPostMetadata.apply(source, r.getString(1), r.getString(0), waterMarkEndValue)
      println("To be Proccessed File Paths :: --> " + d.source + "  <--->  " + d.entityName + "  <--->  " + d.filePath)
      deltaBatchMap += (d -> readData(d))
    })
    Map(deltaBatchMap.toSeq: _*)
  }

  def readData(d: DQPostMetadata): Dataset[Row] = {
    val spark = AdlsAdapter.spark(d.source)
    if (d.filePath.endsWith("tsv"))
      spark.read.format("csv").option("header", "true").option("delimiter", "\t").load(d.filePath)
    else
      spark.read.parquet(d.filePath)
  }
}