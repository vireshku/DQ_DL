package com.ms.jedi.dq.pre.rule.result

import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.pre.rule.valueobject.RulePopulate
import net.liftweb.json.DefaultFormats
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.udf

object RuleEmiter {

  def emit() = {

    System.setProperty("hadoop.home.dir", "c:\\spark");
    val sc = AdlsAdapter.sc("Pre-Process")
    val sqc = AdlsAdapter.sqc("Pre-Process")
    val ss = AdlsAdapter.spark("Pre-Process")
    import sqc.implicits._
    implicit val formats = DefaultFormats

    val data = sqc.read.parquet("adl://psbidevuse2adls01.azuredatalakestore.net/PPE/Raw/Taxonomy/dbo_vwPPGTaxonomyPlan/full/D_vwPPGTaxonomyPlan_v1_2018-08-28-09-00.parquet")
    // println(data.show())

    val resultJson = ""

    val jsonString = write(resultJson)

    println("Initial Count --> " + data.count)

    //val qualifiedData = data.withColumn("DQ_Result", unique(col("Domain")))

    val qualifiedData = data.groupBy(col("Domain")).count()
    //   println(qualifiedData.show())

    //   println(full.show())

  

    val r1 = data.withColumn("r1", nullcheck(col("BusinessScenario")))

    val full = r1.join(qualifiedData, "Domain")
    
      println("After jin count --> " + full.count)

    val taggedData = full.withColumn("DQ_Result", dq(col("count"))).drop(col("count"))
    println(taggedData.show())
    println(" Afer tagging count -->" + taggedData.count())

  }

  def dq = udf((f: Int) => {

    if (f > 8) {
      implicit val formats = DefaultFormats
      val resultJson = ""
      val jsonString = write(resultJson)
      jsonString
    } else {
      implicit val formats = DefaultFormats
      val resultJson = ""
      val jsonString = write(resultJson)
      jsonString
    }
  })

  def nullcheck = udf((f: String) => {
    if (f == "Null") {
      "Nullable"
    } else {
      "Non-Nullable"
    }
  })

  def checkBlank = udf((b: String, c: String) => {
    if (b == "Apps" && c == "No") {
      implicit val formats = DefaultFormats
      val resultJson = ""
      val jsonString = write(resultJson)
      jsonString
    } else "PASS"
  })

  //JMD firt geniune rule

  def unique = udf((c: String) => {
    val sc = AdlsAdapter.sc("Pre-Process")
    val sqc = AdlsAdapter.sqc("Pre-Process")
    val ss = AdlsAdapter.spark("Pre-Process")
    val data = sqc.read.parquet("adl://psbidevuse2adls01.azuredatalakestore.net/PPE/Raw/Taxonomy/dbo_vwPPGTaxonomyPlan/full/D_vwPPGTaxonomyPlan_v1_2018-08-28-09-00.parquet")
    // println(data.show())

    if (data.select(col(c)).distinct().count() > 1) {
      implicit val formats = DefaultFormats
      val resultJson = ""
      val jsonString = write(resultJson)
      jsonString
    } else {
      implicit val formats = DefaultFormats
      val resultJson = ""
      val jsonString = write(resultJson)
      jsonString
    }
  })

  def main(args: Array[String]): Unit = {
    emit()
  }
}