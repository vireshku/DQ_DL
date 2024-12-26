package com.ms.jedi.dq.predict.execution

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

object UniqueRuleExec {

  def execute(df: Dataset[Row], column: String, flag: String) = {

    if (flag.equalsIgnoreCase("true")) {

      val dfCount = df.groupBy(col(column)).count
      //println(dfCount.show())
      val gdf = dfCount.filter("count = " + 1)
      val dirtyData = df.except(gdf.select(col(column)))

      println("Qualified data for Uniqueness Rule-->")
      println(gdf.select(col(column)).distinct().show())

      println("Dirty data for Uniqueness Rule-->")
      println(dirtyData.show())

    } else {

    }

  }
}