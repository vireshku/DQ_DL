package com.ms.jedi.dq.post.rule.metrics

import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.post.rule.writer.DimensionDQWriter
import com.ms.jedi.dq.post.rule.model.DQDimMetric
import com.ms.jedi.dq.post.rule.metrics.support.OverAllDataQualityTrait
import java.sql.Timestamp
import com.ms.jedi.dq.post.rule.watermark.RuleWaterMark

object ValidDimensionMetric extends OverAllDataQualityTrait with RuleWaterMark {

  def metrics(metadata: DQPostMetadata, data: Dataset[Row]) = {

    val sparkSQlContext = AdlsAdapter.sqc(metadata.source)
    val spark = AdlsAdapter.spark(metadata.source)
    import sparkSQlContext.implicits._
    try {
      println("Processing the ValidDimensionMetric data quality metrics.............")
      val calculatedData = processDQDim(data, "Validity")
      if (!calculatedData.take(1).isEmpty) {
        println("ValidDimensionMetric --> IS NON EMPTY")
        val passPerc = calculatedData.map(p => p.mkString("").toDouble).first()
        val metricsData = spark.createDataset(Seq(DQDimMetric.apply(metadata.source, metadata.entityName, passPerc, 1d - passPerc, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()))))
        println("metricsData.............................")
        println(metricsData.show())

        DimensionDQWriter.populateMetrics(metricsData, "ComputeStore.ValidDimensionMetricNeo")
      }
    } catch {
      case e: Exception => println("Problem in ValidDimensionMetric quality metrics for the file --> " + metadata.filePath + " --- " + e.getMessage)
    }
  }

}