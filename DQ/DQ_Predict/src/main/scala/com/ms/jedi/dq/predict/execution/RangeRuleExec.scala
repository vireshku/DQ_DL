package com.ms.jedi.dq.predict.execution

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

object RangeRuleExec {

  def execute(df: Dataset[Row], column: String, validValues: String) = {
    val values = validValues.substring(1, validValues.length() - 1)
    val vdf = df.filter(col(column).>=(values.split(",").apply(0)))
      .filter(col(column).<=(values.split(",").apply(1)))

    val dirtyData = df.except(vdf)

    println("Qualified data for Range Rule-->")
    println(vdf.distinct().show())

    println("Dirty data for Range Rule-->")
    println(dirtyData.show())

  }
}