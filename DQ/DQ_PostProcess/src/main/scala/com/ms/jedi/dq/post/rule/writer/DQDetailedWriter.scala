package com.ms.jedi.dq.post.rule.writer

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SaveMode
import com.ms.jedi.dq.post.rule.model.DQDetailedModel

object DQDetailedWriter {

  def populateMetrics(data: Dataset[Row], tableName: String) = {
    println("OverALL :: Populating the hive table:   " + tableName + " ..............")
    data.write.mode(SaveMode.Append).insertInto(tableName)
    println("OverALL :: Finished: Populating the hive table: " + tableName + " ..............")
  }
}