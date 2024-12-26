package com.ms.jedi.dq.pre.rule.strategy

import com.ms.jedi.dq.pre.rule.result.DataReader
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

object RuleNullCheck {

  def nullCheck(): Dataset[Row] = {
    println("starting with the data quality null check ......")
    val data = DataReader.rawData()
    val r11 = data.withColumn("11", nulludf(col("BusinessScenario")))
    val r14 = r11.withColumn("14", nulludf(col("Domain")))
    r14
  }

  def nulludf = udf((f: String) => {
    if (f == "Null") {
      "Nullable"
    } else {
      "Non-Nullable"
    }
  })

}