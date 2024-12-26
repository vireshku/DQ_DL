package com.ms.jedi.dq.post.rule.metrics

import com.ms.jedi.dq.post.rule.watermark.RuleWaterMark
import com.ms.jedi.dq.post.rule.metrics.support.OverAllDataQualityTrait
import org.apache.spark.sql.Dataset
import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import org.apache.spark.sql.Row
import com.ms.db.adapter.AdlsAdapter
import scala.util.control.Exception.Catch
import com.ms.jedi.dq.post.rule.writer.DimensionDQWriter
import com.ms.jedi.dq.post.rule.model.DQDetailedModel
import java.sql.Timestamp
import org.apache.spark.sql.functions._
import com.ms.jedi.dq.post.rule.writer.DQDetailedWriter

object DQDetailedMetric extends OverAllDataQualityTrait with RuleWaterMark {

  def metrics(metadata: DQPostMetadata, data: Dataset[Row]) = {

    val sparkSQlContext = AdlsAdapter.sqc(metadata.source)
    val spark = AdlsAdapter.spark(metadata.source)
    import sparkSQlContext.implicits._
    println("Processing the Detailed Metric data quality metrics.............> " + metadata.filePath)

    var dataResult = Seq[DQDetailedModel]()

    try {
      val detailedOutput = processDetailedResult(data)
      val metricsData = detailedOutput.withColumn("Source", lit(metadata.source)).withColumn("EntityName", lit(metadata.entityName)).withColumn("LakePath", lit(metadata.filePath))
        .withColumn("DQAppliedCreatedDate", lit(new Timestamp(System.currentTimeMillis()))).withColumn("DQAppliedModifiedDate", lit(new Timestamp(System.currentTimeMillis())))
      println("metricsData.............................")
      println(metricsData.show())
      DQDetailedWriter.populateMetrics(metricsData, "ComputeStore.DQDetailedMetricsNew")
    } catch {
      case e: Exception => println("Problem in detailed data quality metrics for the file --> " + metadata.filePath + " --- " + e.getMessage)
    }

  }

}