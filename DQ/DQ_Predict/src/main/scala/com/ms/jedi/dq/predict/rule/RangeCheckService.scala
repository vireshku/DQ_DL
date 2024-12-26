package com.ms.jedi.dq.predict.rule

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.functions._
import org.apache.spark.sql.catalyst.expressions.Ascending
import com.ms.db.adapter.AdlsAdapter

object RangeCheckService {

  def doCheck(vdfList: List[Dataset[Row]], columns: List[String]) = {
    //    println("do range check......")

    val lowerQuartile = 0.5
    val upperQuartile = 99.5
    val lambda = 0.2
    val multiplier = 3.0

    columns.foreach { column =>
      println("column name --- > " + column)
      
      var lowerPerctentileBuffer = new ListBuffer[Double]()
      var upperPerctentileBuffer = new ListBuffer[Double]()
      
      println("lowerPerctentileBuffer --> " + lowerPerctentileBuffer.mkString(","))
      println("upperPerctentileBuffer --> " + upperPerctentileBuffer.mkString(","))

      vdfList.foreach { vdf =>
        println("data set")
        //println(vdf.show())
        lowerPerctentileBuffer += computePercentile(vdf.select(column), lowerQuartile)
        upperPerctentileBuffer += computePercentile(vdf.select(column), upperQuartile)
      }

      val spark = AdlsAdapter.spark("DQ-Predict")
      import spark.implicits._

      val lowerPerctentileDF = lowerPerctentileBuffer.toDF()
      val upperPerctentileDF = upperPerctentileBuffer.toDF()

//      println(lowerPerctentileDF.show())
//      println(upperPerctentileDF.show())
//
//      println(" lowerSTD -->  " + lowerPerctentileDF.agg(stddev(col("value"))).head().getDouble(0))
//      println(" upperSTD -->  " + upperPerctentileDF.agg(stddev(col("value"))).head().getDouble(0))
//      println(" lowerEWMA ---> " + deducingMovingAvg(lowerPerctentileBuffer.toList))
//      println(" upperEWMA ---> " + deducingMovingAvg(upperPerctentileBuffer.toList))

      val lowerBoundRange = deducingMovingAvg(lowerPerctentileBuffer.toList) - multiplier * lowerPerctentileDF.agg(stddev(col("value"))).head().getDouble(0)
      val upperBoundRange = deducingMovingAvg(upperPerctentileBuffer.toList) + multiplier * upperPerctentileDF.agg(stddev(col("value"))).head().getDouble(0)

      //println("data range is --> " + lowerBoundRange + "   ,   " + upperBoundRange)
      val r = "QK_Rule " + column + " RangeCheck" + " [" + Math.round(lowerBoundRange) + "," + Math.round(upperBoundRange) + "]"
      println("Range Check Rule --> " + r)

      lowerPerctentileBuffer.clear()
      upperPerctentileBuffer.clear()
    }
  }

  def computePercentile(vdf: Dataset[Row], factor: Double): Double = {
    val spark = AdlsAdapter.spark("DQ-Predict")
    import spark.implicits._

    var r = vdf.map(x => { if (x(0) != null && !x(0).toString().isEmpty() && !x(0).toString().equalsIgnoreCase("null")) { x(0).toString().toDouble } else { 0.0 } }).rdd.sortBy(x => x)
    //var r = vdf.map(x=>{x(0).toString().toDouble }).rdd.sortBy(x => x)

    var c = r.count()

    if (c == 1) r.first()
    else {
      val n = ((factor / 100d) * (c + 1d))
      val k = math.floor(n).toLong
      val d = n - k

      if (k <= 0) r.first()
      else {
        val index = r.zipWithIndex().map(_.swap)
        val last = c
        if (k <= c)
          index.lookup(last - 1).head
        else
          index.lookup(k - 1).head + d * (index.lookup(k).head - index.lookup(k - 1).head)
      }
    }
  }

  def deducingMovingAvg(nullPercCollection: List[Double]): Double = {

    var prev_ewma = 0.0
    var ewma = 0.0
    val lambda = .2
    for (perc <- nullPercCollection) {
      if (prev_ewma == 0.0) {
        ewma = perc
      } else
        ewma = perc * lambda + (1.0 - lambda) * prev_ewma
      prev_ewma = ewma
    }
    return ewma
  }

}