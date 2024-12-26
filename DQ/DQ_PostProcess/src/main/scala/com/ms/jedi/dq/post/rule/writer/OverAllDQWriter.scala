package com.ms.jedi.dq.post.rule.writer

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.jedi.dq.post.rule.model.DQMetricOverAll
import org.apache.spark.sql.SaveMode
import com.ms.jedi.dq.post.rule.watermark.RuleWaterMark

object OverAllDQWriter extends RuleMetricsWriter {

  def populateMetrics(data: Dataset[DQMetricOverAll]) = {
    println("Populating the hive table: DQMetricOverAllNeo ..............")
    data.write.mode(SaveMode.Append).insertInto("ComputeStore.DQMetricOverAllNeo")
    println("Finished: Populating the hive table: DQMetricOverAllNeo ..............")

  }

  def populateMetrics(data: Dataset[Row], tableName: String) = {
    println("OverALL :: Populating the hive table:   " + tableName + " ..............")
    data.write.mode(SaveMode.Overwrite).insertInto(tableName)
    println("OverALL :: Finished: Populating the hive table: " + tableName + " ..............")

  }
}