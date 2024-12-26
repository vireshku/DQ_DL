package com.ms.jedi.dq.exec.rule.strategy

/**
 * Algorithm implementation for null check,based on strategy pattern
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
import scala.collection.mutable.ListBuffer
import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.WrappedArray
import org.apache.spark.broadcast.Broadcast

object RuleNullCheck {

  def check(id: String, rule: String, d: Dataset[Row]): Dataset[Row] = {
    val columns = rule.split(" ").apply(3)
    val entity = rule.split(" ").apply(2)
    println("starting with the data quality null check with audit change ......")
    val seed = AdlsAdapter.sc(entity).broadcast(nullValues(rule))
    val r = d.withColumn(id.toString(), nulludf(seed)(col(columns)))
    println(r.show())
    r.select("jId", id.toString())
  }

  def nulludf(seed: Broadcast[Array[String]]) = udf((f: String) => {
    if(f == null){
      0
    }
    else if (seed.value.contains(f)) {
      0
    } else {
      1
    }
  })


  def nullValues(rule: String): Array[String] = {
    val d = rule.split(" ")
    var vList = ArrayBuffer[String]()
    if (d.length > 4) {
      vList = vList ++ d.apply(4).substring(1, d.apply(4).length() - 1).split(",")
    }
    vList += "Null"
    vList += null
    vList += "null"
    println(vList.mkString("-----------"))
    vList.toArray
  }

  def main(args: Array[String]): Unit = {
    val ss = AdlsAdapter.spark("")
    val someData = Seq(
      Row(8, "Null"),
      Row(64, "#Null#"),
      Row(-27, "horse"))
    val someSchema = List(
      StructField("number", IntegerType, true),
      StructField("word", StringType, true))
    val someDF = ss.createDataFrame(
      ss.sparkContext.parallelize(someData),
      StructType(someSchema))
    check("111", "DQ_Rule NullCheck someData word [#Null#]", someDF)
  }
}