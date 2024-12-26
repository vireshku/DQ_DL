package com.ms.jedi.dq.post.rule.writer

import org.apache.spark.sql.Dataset
import com.ms.jedi.dq.post.rule.model.DQPerSubAreaModel
import org.apache.spark.sql.SaveMode
import com.ms.jedi.dq.post.rule.model.DQPerSourceModel
import com.ms.jedi.dq.post.rule.model.DQSourceSubAreaModel

object DQPerSourceWriter {

  def populateMetrics(data: Dataset[DQSourceSubAreaModel], dimName: String) = {

    println("Populating the hive table: " + dimName + " ..............")
    data.write.mode(SaveMode.Append).insertInto(dimName)
    println("Finished: Populating the hive table: " + dimName + " ..............")
  }
}