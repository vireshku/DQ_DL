package com.ms.jedi.dq.predict.rule

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.mllib.linalg.Vector



object StatsCalc extends SparkEnv {
  
  def mean(vd: Dataset[Row]): Double={
    
    val m = mean(vd)
    println("Mean Value -- > " + m)
    m
  }
  
  def mode(vdf: Dataset[Row]): Double={
      val spark = getSession
    import spark.implicits._
   // val vectorDF = vdf.map(x => { org.apache.spark.mllib.linalg.Vector.apply[Any](x(0)) })
    
    val assembler = new VectorAssembler().setInputCols(Array("DealCategory")).setOutputCol("DealCategory")
    val vd = assembler.transform(vdf)
    
    //Statistics.colStats()
    0.0
  }
  
}