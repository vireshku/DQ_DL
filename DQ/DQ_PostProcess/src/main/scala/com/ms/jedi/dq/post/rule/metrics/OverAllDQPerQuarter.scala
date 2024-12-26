package com.ms.jedi.dq.post.rule.metrics

import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.post.rule.writer.OverAllDQWriter
import org.apache.spark.sql.functions._

object OverAllDQPerQuarter {

  def metrics(source: String) = {
    println("Processing the metrics OverAllDQPerQuarter...........")
    val spark = AdlsAdapter.spark(source)
    val data = spark.table("computeStore.DQMetricOverAllNeo").filter(col("Source") === source).filter("DQAppliedCreatedDate >=NOW() - Interval 3 month")
    println(data.show())
    OverAllDQWriter.populateMetrics(data, "ComputeStore.DQMetricOverAllPerQuarterNeo")
  }

}