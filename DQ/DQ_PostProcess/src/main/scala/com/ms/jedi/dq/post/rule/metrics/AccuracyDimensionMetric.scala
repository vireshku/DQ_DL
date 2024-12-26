package com.ms.jedi.dq.post.rule.metrics

import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.jedi.dq.post.rule.metrics.support.OverAllDataQualityTrait
import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.post.rule.writer.DimensionDQWriter
import com.ms.jedi.dq.post.rule.model.DQDimMetric
import java.sql.Timestamp
import com.ms.jedi.dq.post.rule.watermark.RuleWaterMark

object AccuracyDimensionMetric extends OverAllDataQualityTrait with RuleWaterMark {

  def metrics(metadata: DQPostMetadata, data: Dataset[Row]) = {

    val sparkSQlContext = AdlsAdapter.sqc(metadata.source)
    val spark = AdlsAdapter.spark(metadata.source)
    import sparkSQlContext.implicits._
    try {
      println("Processing the AccuracyDimensionMetric data quality metrics.............")

      val calculatedData = processDQDim(data, "Accuracy")
      if (!calculatedData.take(1).isEmpty) {
        println("AccuracyDimensionMetric --> IS NON EMPTY")
        val passPerc = calculatedData.map(p => p.mkString("").toDouble).first()
        val metricsData = spark.createDataset(Seq(DQDimMetric.apply(metadata.source, metadata.entityName, passPerc, 1d - passPerc, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()))))
        println("metricsData.............................")
        println(metricsData.show())
        DimensionDQWriter.populateMetrics(metricsData, "ComputeStore.AccuracyDimensionMetricNeo")
      }
    } catch {
      case e: Exception => println("Problem in AccuracyDimensionMetric quality metrics for the file --> " + metadata.filePath + " --- " + e.getMessage)
    }
  }

}