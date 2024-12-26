package com.ms.jedi.dq.post.rule.writer

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SaveMode

trait RuleMetricsWriter {

  def write(data: Dataset[Any], hiveTable: String) = {
    // write to the given Hive table.....
    data.write.mode(SaveMode.Append).saveAsTable(hiveTable);
  }
}