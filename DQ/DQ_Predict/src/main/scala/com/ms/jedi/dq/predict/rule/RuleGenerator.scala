package com.ms.jedi.dq.predict.rule

import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.IntegerType
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.types.DoubleType
import com.ms.db.adapter.AdlsAdapter

object RuleGenerator {

  System.setProperty("hadoop.home.dir", "c:\\spark");
  println("Starting the process for Rule Generation........")

  val input = JsonReader.input
  val catColumn = ListBuffer[String]()
  val conColumn = ListBuffer[String]()
  var allColumn = ListBuffer[String]()

  input.MetaData.foreach { kv =>
    if (kv._2.equalsIgnoreCase("Categorical")) {
      catColumn += kv._1
    } else {
      conColumn += kv._1
    }
  }

  allColumn = catColumn ++ conColumn

  val sc = AdlsAdapter.sc("DQ-Predict")
  val sqlContext = AdlsAdapter.sqc("DQ-Predict")
  val spark = AdlsAdapter.spark("DQ-Predict")
  import sqlContext.implicits._
  var collatedDataCollection = ListBuffer[Dataset[Row]]()
  var scheme = ArrayBuffer[StructField]()
  input.MetaData.foreach { kv =>
    if (kv._2.equalsIgnoreCase("Categorical")) {
      scheme += (StructField(kv._1, StringType, true))
    } else {
      scheme += (StructField(kv._1, DoubleType, true))
    }
  }

  val schema = StructType(scheme)
  //var initialDataSet = getSession.emptyDataFrame.withColumn("DealCategory", lit(""))

  var initialDataSet = spark.createDataFrame(sc.emptyRDD[Row], schema)

  input.DataSets.foreach { dataPoint =>
    val vdf = sqlContext.read.format("com.databricks.spark.csv")
      .option("header", "true").load(dataPoint)
    // println(" data point count --> " + vdf.count())
    collatedDataCollection += vdf
    initialDataSet = initialDataSet.union(vdf)
    //println(vdf.show())

  }

  //println("List size --> " + collatedDataCollection.toList.size)
  // print(initialDataSet.count())

  //print(initialDataSet.show())
  // Data Processing
  
  ValidationService.doCheck(initialDataSet, catColumn.toList)
  NullCheckService.doCheck(collatedDataCollection.toList, allColumn.toList)
  UniquenessService.doCheck(collatedDataCollection.toList, allColumn.toList)
  RangeCheckService.doCheck(collatedDataCollection.toList, conColumn.toList)

}