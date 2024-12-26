package com.ms.jedi.dq.predict.execution

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.predict.rule.JsonReader
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.IntegerType

object RuleExecutor {

  System.setProperty("hadoop.home.dir", "c:\\spark");
  val sc = AdlsAdapter.sc("DQ-Predict")
  val sqlContext = AdlsAdapter.sqc("DQ-Predict")
  val spark = AdlsAdapter.spark("DQ-Predict")
  val jobDetail = JsonReader.input()

  var scheme = ArrayBuffer[StructField]()
  jobDetail.MetaData.foreach { kv =>

    if (kv._2.equalsIgnoreCase("Categorical")) {
      scheme += (StructField(kv._1, StringType, true))
    } else {
      scheme += (StructField(kv._1, IntegerType, true))
    }
  }

  val schema2 = StructType(scheme)
  var collatedDataCollection = ListBuffer[Dataset[Row]]()

  var initialDataSet = spark.createDataFrame(sc.emptyRDD[Row], schema2)

  jobDetail.DataSets.foreach { dataPoint =>
    val vdf = sqlContext.read.format("com.databricks.spark.csv")
      .option("header", "true").load(dataPoint)
    collatedDataCollection += vdf
    initialDataSet = initialDataSet.union(vdf)
    //println(vdf.show())

  }
  // print(initialDataSet.count())

  //print(initialDataSet.show())
  // Data Processing

  jobDetail.Rules.PredictedRules.foreach { rule =>

    rule.split(" ").apply(2) match {
      case "NullCheck"  => NullRuleExec.execute(initialDataSet, rule.split(" ").apply(1), rule.split(" ").apply(3))
      case "ValidCheck" => ValidRuleExec.execute(initialDataSet, rule.split(" ").apply(1), rule.split(" ").apply(4))
      case "UniqCheck"  => UniqueRuleExec.execute(initialDataSet, rule.split(" ").apply(1), rule.split(" ").apply(3))
      case "RangeCheck" => RangeRuleExec.execute(initialDataSet, rule.split(" ").apply(1), rule.split(" ").apply(4))
    }
  }

  jobDetail.Rules.StatisticalRule.foreach { rule =>

    rule.split(" ").apply(2) match {
      case "Mean"   => StatsRuleExec.mean(initialDataSet)
      case "Mode"   => StatsRuleExec.mode
      case "Median" => StatsRuleExec.median(initialDataSet)

    }

  }

  jobDetail.Rules.BuisnessRule.foreach { rule =>

    rule.split(" ").apply(2) match {
      case "CATEGORY" => BuisnessRuleExec.execute
      case _          => ""
    }

  }
}