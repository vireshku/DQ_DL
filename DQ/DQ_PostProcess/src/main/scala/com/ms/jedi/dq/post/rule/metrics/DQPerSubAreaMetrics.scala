package com.ms.jedi.dq.post.rule.metrics

import com.ms.jedi.dq.post.rule.metrics.support.OverAllDataQualityTrait
import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import java.sql.Timestamp
import com.ms.jedi.dq.post.rule.model.DQPerSubAreaModel
import com.ms.jedi.dq.post.rule.writer.DQPerSubAreaWriter
import org.apache.spark.sql.functions._
import com.ms.jedi.dq.post.rule.watermark.RuleWaterMark
import com.ms.jedi.dq.post.rule.model.DQSourceSubAreaModel

object DQPerSubAreaMetrics extends OverAllDataQualityTrait with RuleWaterMark {

  def metrics(metadata: DQPostMetadata, data: Dataset[Row]) = {

    val sparkSQlContext = AdlsAdapter.sqc(metadata.source)
    val spark = AdlsAdapter.spark(metadata.source)
    import sparkSQlContext.implicits._
    println("Processing the per subject area data quality metrics.............> " + metadata.filePath)

    try {
      val pocessedData = processSubAreaResult(data)
      println(pocessedData.show())
      val subArea = pocessedData.select(col("subjectArea")).map(p => p.mkString("")).first()
      val passPerc = pocessedData.select(col("result")).map(p => p.mkString("").toDouble).first()
      val metricsData = spark.createDataset(Seq(DQSourceSubAreaModel.apply(metadata.source,subArea, passPerc, 1d - passPerc, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()))))
      println("metricsData.............................")
      println(metricsData.show())
      DQPerSubAreaWriter.populateMetrics(metricsData, "ComputeStore.DQPerSourceMetricsNeo2")
    } catch {
      case e: Exception => println("Problem in subject area data quality metrics for the file --> " + metadata.filePath + " --- " + e.getMessage)
    }

  }

  def main(args: Array[String]): Unit = {
    metrics(null, null)

  }
}