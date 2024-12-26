package com.ms.jedi.dq.pre.rule.strategy

import com.ms.jedi.dq.pre.rule.result.DataReader
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import scala.collection.mutable.ListBuffer
import com.ms.jedi.dq.pre.rule.result.RuleOutput
import com.ms.jedi.dq.pre.rule.result.RuleExecMetadata
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object RuleDqOutput {
  
   def resultJson(data: Dataset[Row]):Dataset[Row] = {
     println("starting with the final data quality result metadata creation ......")
    val taggedData = data.withColumn("DQ_Result", dq(col("11"), col("12"), col("13"), col("14"))).drop(("11"), ("12"), ("13"), ("14"))
    
    val cccc = taggedData.filter(col("12") ==="Unique").filter(col("SMSGPriority") === "Other").select(col("DQ_Result")).first()
    println(taggedData.show())
    println("one of the result jsons produced for one of the records...")
    println(cccc)
    taggedData
  }
   

  def dq = udf((a: String, b: String, c: String, d: String) => {

    val roList = new ListBuffer[RuleOutput]
    implicit val formats = DefaultFormats

    if (a == "Nullable") {
      val ro1 = RuleOutput.apply(11, 0, "Nullable")
      roList.append(ro1)
    }
    if (a == "Non-Nullable") {
      val ro2 = RuleOutput.apply(11, 1, "Non-Nullable")
      roList.append(ro2)
    }
    if (d == "Nullable") {
      val ro3 = RuleOutput.apply(14, 0, "Nullable")
      roList.append(ro3)
    }
    if (d == "Non-Nullable") {
      val ro4 = RuleOutput.apply(14, 1, "Non-Nullable")
      roList.append(ro4)
    }
    if (b == "Duplicate") {
      val ro5 = RuleOutput.apply(12, 0, "Duplicate")
      roList.append(ro5)
    }
    if (b == "Unique") {
      val ro6 = RuleOutput.apply(12, 1, "Unique")
      roList.append(ro6)
    }
    if (c == "Unique") {
      val ro7 = RuleOutput.apply(13, 1, "Unique")
      roList.append(ro7)
    }
    if (c == "Duplicate") {
      val ro8 = RuleOutput.apply(13, 0, "Duplicate")
      roList.append(ro8)
    }
    //println(roList.size)
    val r = RuleExecMetadata.apply(roList.toList)
    val jsonString = write(r)
   // println(jsonString)
    jsonString
  })
  
}