package com.ms.jedi.dq.predict.rule

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.functions._
import com.ms.db.adapter.AdlsAdapter

object NullCheckService {

  def doCheck(vdfList: List[Dataset[Row]],columns:List[String]) = {
//    println("do null check ....")
    
    columns.foreach{ column =>
    val nullPercCollection = ListBuffer[Double]();
    vdfList.foreach { vdf =>
      val total = vdf.count()
      val nullCount = vdf.filter(vdf.col(column).isNull).count()
      val nullPerc = nullCount * 100 / total
      nullPercCollection += nullPerc
    }

    checkEligiblity(nullPercCollection.toList,column)
  }
}

  def checkEligiblity(nullPercCollection: List[Double],column:String) = {
    val spark = AdlsAdapter.spark("DQ-Predict")
    import spark.implicits._

    val percDf = nullPercCollection.toDF()
    val standardDeviation = percDf.agg(stddev(col("value"))).head().getDouble(0)
    val ewma = deducingMovingAvg(nullPercCollection)

    val stdDeviationCoefficient = 2.8
    val maxThreshold = 1
    val lambda = .2

    if (ewma + (stdDeviationCoefficient * standardDeviation) < maxThreshold) {
      val r = "QK_Rule "+column+" NullCheck False "
      println("Null check Rule --> " + r)
    } else {
      val r = "QK_Rule "+column+" NullCheck True"
      println("Null check Rule --> " + r)
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