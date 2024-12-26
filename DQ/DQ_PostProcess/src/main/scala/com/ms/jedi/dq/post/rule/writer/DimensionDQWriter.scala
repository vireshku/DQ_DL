package com.ms.jedi.dq.post.rule.writer

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SaveMode
import com.ms.jedi.dq.post.rule.model.DQDimMetric

object DimensionDQWriter extends RuleMetricsWriter {

  def populateMetrics(data: Dataset[DQDimMetric], dimName: String) = {

    println("Populating the hive table: " + dimName + " ..............")
    data.write.mode(SaveMode.Append).insertInto(dimName)
    println("Finished: Populating the hive table: " + dimName + " ..............")

  }

}