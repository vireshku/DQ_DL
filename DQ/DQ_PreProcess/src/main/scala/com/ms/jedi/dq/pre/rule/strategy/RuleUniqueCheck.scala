package com.ms.jedi.dq.pre.rule.strategy

import com.ms.jedi.dq.pre.rule.result.DataReader
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

object RuleUniqueCheck {

  def uniqueCheck(data: Dataset[Row]): Dataset[Row] = {
    println("starting with the data quality null check ......")

    val qualifiedData = data.groupBy(col("Domain")).count()
    val full = data.join(qualifiedData, "Domain")
    val r13 = full.withColumn("13", uniqueudf(col("count"))).drop(col("count"))
    val BusinessScenarioData = r13.groupBy(col("BusinessScenario")).count()
    val fullBusinessScenario = r13.join(BusinessScenarioData, "BusinessScenario")
    val r12 = fullBusinessScenario.withColumn("12", uniqueudf(col("count"))).drop(col("count"))
    //println(r12.columns.mkString("-----"))
    r12
  }

  def uniqueudf = udf((f: Int) => {
    if (f > 1) {
      "Duplicate"
    } else {
      "Unique"
    }
  })

}