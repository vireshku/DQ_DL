package com.ms.jedi.dq.post.rule.metrics

import com.ms.jedi.dq.post.rule.metrics.support.OverAllDataQualityTrait
import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.post.rule.model.DQMetricOverAll
import com.ms.jedi.dq.post.rule.writer.OverAllDQWriter
import java.sql.Timestamp
import com.ms.jedi.dq.post.rule.model.DQPerSourceModel
import com.ms.jedi.dq.post.rule.writer.DQPerSourceWriter
import com.ms.jedi.dq.post.rule.watermark.RuleWaterMark
import org.apache.spark.sql.functions._
import com.ms.jedi.dq.post.rule.model.DQSourceSubAreaModel

object DQPerSourceMetrics extends OverAllDataQualityTrait with RuleWaterMark {

  def metrics(metadata: DQPostMetadata, data: Dataset[Row]) = {

    val sparkSQlContext = AdlsAdapter.sqc(metadata.source)
    val spark = AdlsAdapter.spark(metadata.source)
    import sparkSQlContext.implicits._
    println("Processing the per source data quality metrics for path.............> " + metadata.filePath)
    try {
      val passPerc = processSourceResult(data)._1.map(p => p.mkString("").toDouble).first()
      val subjectArea = processSourceResult(data)._2.map(p => p.mkString("")).first()
      val metricsData = spark.createDataset(Seq(DQSourceSubAreaModel.apply(metadata.source, subjectArea, passPerc, 1d - passPerc, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()))))
      println("metricsData.............................")
      println(metricsData.show())
      DQPerSourceWriter.populateMetrics(metricsData, "ComputeStore.DQPerSourceMetricsNeo2")
    } catch {
      case e: Exception => println("Problem in source data quality metrics for the file --> " + metadata.filePath + " --- " + e.getMessage)
    }
  }
}