package com.ms.jedi.dq.predict.rule

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import scala.collection.mutable.ListBuffer

object UniquenessService {

  def doCheck(vdfList: List[Dataset[Row]],columns:List[String]) = {
    //println("do unique check....")

    columns.foreach{ column =>
    val uniquenessPercCollection = ListBuffer[Double]()

    vdfList.foreach { vdf =>

      val distinctCount = vdf.agg(countDistinct(column))
      //println(distinctCount.show())
      val totalCount = vdf.agg(count(column))
      //println(totalCount.show())
      val perc = deducePercentage(distinctCount.first().getLong(0), totalCount.first().getLong(0))
      uniquenessPercCollection += perc
    }

    val min = uniquenessPercCollection.min
    if (min >= 90) {
      val r = "QK_Rule "+ column +" UniqCheck True"
      println("Unique Check Rule --> " + r)
    } else {
      val r = "QK_Rule "+column+" UniqCheck False"
      println("Unique Check Rule --> " + r)
    }
  }
}

  def deducePercentage(distinctCount: Long, totalCount: Long): Double = {

    if (totalCount != 0) {
      val xyz = (distinctCount * 100d / totalCount).toDouble
      return (distinctCount * 100d / totalCount).toDouble
    } else
      return 0.0
  }
}