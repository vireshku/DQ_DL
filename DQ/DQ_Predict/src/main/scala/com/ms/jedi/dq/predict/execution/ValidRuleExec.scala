package com.ms.jedi.dq.predict.execution

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

object ValidRuleExec {

  def execute(df: Dataset[Row], column: String, validValues: String) = {
    val values = validValues.substring(1, validValues.length() - 1)
    val vdf = df.filter(col(column).isin(values.split(","): _*))

    val vdf2 = df.except(vdf)

    println("Qualified data for Valid Rule-->")
    println(vdf.distinct().show())

    println("Dirty data for Valid Rule-->")
    println(vdf2.show())

  }
}