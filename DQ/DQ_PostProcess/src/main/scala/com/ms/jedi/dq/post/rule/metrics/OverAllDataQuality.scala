package com.ms.jedi.dq.post.rule.metrics

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import com.ms.jedi.dq.post.rule.writer.OverAllDQWriter
import com.ms.jedi.dq.post.rule.model.DQMetricOverAll
import com.ms.db.adapter.AdlsAdapter
import java.sql.Timestamp
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.ArrayType
import org.apache.spark.sql.types.StringType
import scala.util.Try
import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import com.ms.jedi.dq.post.rule.metrics.support._
import com.ms.jedi.dq.post.rule.watermark.RuleWaterMark

object OverAllDataQuality extends OverAllDataQualityTrait with RuleWaterMark{

  def metrics(metadata: DQPostMetadata, data: Dataset[Row]) = {
    val sparkSQlContext = AdlsAdapter.sqc(metadata.source)
    val spark = AdlsAdapter.spark(metadata.source)
    import sparkSQlContext.implicits._
    println("Processing the overall data quality metrics.............> " + metadata.filePath)
    try {
      val passPerc = processResult(data).map(p => p.mkString("").toDouble).first()
      val metricsData = spark.createDataset(Seq(DQMetricOverAll.apply(metadata.source, metadata.entityName, passPerc, 1d - passPerc, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()))))
      println("metricsData.............................")
      println(metricsData.show())
      OverAllDQWriter.populateMetrics(metricsData)
    } catch {
      case e: Exception => println("Problem in overall area data quality metrics for the file --> " + metadata.filePath + " --- " + e.getMessage)
    }
  }
}