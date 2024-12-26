package com.ms.jedi.dq.post.rule.metrics.support

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.ArrayType
import org.apache.spark.sql.types.StringType
import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DecimalType

trait OverAllDataQualityTrait {

  def dqResultSchema(): StructType = {
    new StructType()
      .add("version", IntegerType)
      .add(
        "RuleOutput",
        ArrayType(
          new StructType()
            .add("ruleId", StringType)
            .add("result", IntegerType)
            .add("category", StringType)
            .add("subjectArea", StringType)
            .add("dimension", StringType)))
  }

  def processResult(data: Dataset[Row]): Dataset[Row] = {
    val jsDF = data.withColumn("DQ_Json_Results", from_json(data("DQ_Result"), dqResultSchema()))
    val explodeDF = jsDF.withColumn("explodeCols", explode(jsDF("DQ_Json_Results.RuleOutput"))).select("explodeCols.*")
    val rid = explodeDF.select("ruleId", "result", "category", "subjectArea").groupBy("ruleId", "category", "subjectArea").agg(avg("result").alias("result"))
    rid.select((sum("result") / (rid.count())).alias("PassPerc"))
  }

  def processDQDim(data: Dataset[Row], dimension: String): Dataset[Row] = {
    val jsDF = data.withColumn("DQ_Json_Results", from_json(data("DQ_Result"), dqResultSchema()))
    val explodeDF = jsDF.withColumn("explodeCols", explode(jsDF("DQ_Json_Results.RuleOutput"))).select("explodeCols.*").where(col("dimension").eqNullSafe(dimension))
    val rid = explodeDF.select("ruleId", "result", "category", "subjectArea").groupBy("ruleId", "category", "subjectArea").agg(avg("result").alias("result"))
    if (!rid.take(1).isEmpty)
      rid.select((sum("result") / (rid.count())).alias("PassPerc"))
    else
      rid
  }

  def processSourceResult(data: Dataset[Row]): (Dataset[Row], Dataset[Row]) = {
    val jsDF = data.withColumn("DQ_Json_Results", from_json(data("DQ_Result"), dqResultSchema()))
    val explodeDF = jsDF.withColumn("explodeCols", explode(jsDF("DQ_Json_Results.RuleOutput"))).select("explodeCols.*")
    val rid = explodeDF.select("ruleId", "result", "category", "subjectArea").groupBy("ruleId", "category", "subjectArea").agg(avg("result").alias("result"))
    (rid.select((sum("result") / (rid.count())).alias("PassPerc")), rid.select(col("subjectArea")))
  }

  def processSubAreaResult(data: Dataset[Row]): Dataset[Row] = {
    val jsDF = data.withColumn("DQ_Json_Results", from_json(data("DQ_Result"), dqResultSchema()))
    val explodeDF = jsDF.withColumn("explodeCols", explode(jsDF("DQ_Json_Results.RuleOutput"))).select("explodeCols.*")
    val rid = explodeDF.select("subjectArea", "result").groupBy("subjectArea").agg(avg("result").alias("result"))
    rid
  }

  def processDetailedResult(data: Dataset[Row]): Dataset[Row] = {
    val jsDF = data.withColumn("DQ_Json_Results", from_json(data("DQ_Result"), dqResultSchema()))
    val explodeDF = jsDF.withColumn("explodeCols", explode(jsDF("DQ_Json_Results.RuleOutput"))).select("explodeCols.*")
    val outCome = explodeDF.where(col("result") === 0).select(col("ruleId"), col("result"), col("rule"), col("subjectArea")).distinct()
    outCome
  }
}