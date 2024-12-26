package com.ms.jedi.dq.predict.execution

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

object NullRuleExec {

  def execute(df: Dataset[Row], column:String, flag: String) = {

    if (flag.equalsIgnoreCase("true")) {

      val notnulldf = df.filter(col(column).isNotNull)
      val dirtyData = df.except(notnulldf)

      println("Qualified data for Null Rule-->")
      println(notnulldf.show())

      println("Dirty data for Null Rule-->")
      println(dirtyData.show())

    } else {

    }

  }
}