package com.ms.jedi.dq.predict.execution

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.avg
import org.apache.spark.sql.functions.col

import com.ms.db.adapter.AdlsAdapter

object StatsRuleExec {

  def mean(df: Dataset[Row]): Double = {

    val me = df.agg(avg(col("DealCategory"))).first().getDouble(0)
    println("Mean is -->  " + me)
    me
  }

  def mode = {

  }

  def median(df: Dataset[Row]): Double  = {
    val spark = AdlsAdapter.spark("DQ-Predict")
    df.createOrReplaceTempView("df")
    val adf = spark.sql("select percentile_approx(DealCategory, 0.5) as median from df")
    //println(adf.show())
    println("Median is --> " + adf.first().getDouble(0))
    adf.first().getDouble(0)

  }
}