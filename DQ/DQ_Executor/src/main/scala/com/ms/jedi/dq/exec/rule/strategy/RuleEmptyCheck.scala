package com.ms.jedi.dq.exec.rule.strategy

/**
 * Algorithm implementation for uniqueness check,based on strategy pattern
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField

object RuleEmptyCheck {

  def check(id: String, rule: String, d: Dataset[Row]): Dataset[Row] = {
    val c = rule.split(" ").apply(3)
    println("starting with the data quality empty check ......")
    val data = d
    val colList = c.split(",")
    val qualifiedData = data.groupBy(colList map col: _*).count()
    val full = data.join(qualifiedData, colList)
    val rData = full.withColumn(id.toString(), uniqueudf(col("count"))).drop(col("count"))
    rData.select("jId", id.toString())
  }

  def uniqueudf = udf((f: Int) => {
    if (f > 1) {
      0
    } else {
      1
    }
  })
}