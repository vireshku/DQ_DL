package com.ms.jedi.dq.predict.rule
import org.apache.spark._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Encoders

object ValidationService {

  def doCheck(vdf: Dataset[Row], column: List[String]) = {
    //println("do validation check......")

    column.foreach { colName =>

      val withCountvdf = vdf.groupBy(colName).count()
      //println(withCountvdf.show())

      val withPrecvdf = withCountvdf.withColumn("total", lit(vdf.count())).withColumn("percentage", expr("count*100/total"))
      //println(withPrecvdf.show())

      val percThreshold = JsonReader.input().Config.ValidParams.get("percThreshold").get
      val filteredvdf = withPrecvdf.filter("percentage >" + percThreshold).orderBy(desc(colName))

      //    println(filteredvdf.show())

      // Check Eligiblity
      val catMinThreshold = JsonReader.input().Config.ValidParams.get("catMinThreshold").get // Assuming if categorical,then at least no. of categories = 2
      val genCatThreshold = JsonReader.input().Config.ValidParams.get("genCatThreshold").get // Assuming if categorical,then at maximum no. of categories = 50
      val totalMinCoverage = JsonReader.input().Config.ValidParams.get("totalMinCoverage").get // Total categories covered after the filtering should be 99%
      val count = filteredvdf.count()
      //    println(" count --> " + count)
      //    println(filteredvdf.agg(sum("percentage")).first().getDouble(0))

      if (count <= genCatThreshold && count >= catMinThreshold &&
        filteredvdf.agg(sum("percentage")).first().getDouble(0) > totalMinCoverage) {
        //println(filteredvdf.show())
        // println("valid Result --> " + filteredvdf.select("DealCategory").collect().mkString("---"))

        val validValues = filteredvdf.select(colName).as(Encoders.STRING).rdd.collect().mkString(",")

        //val validValues = filteredvdf.select(colName).collect().mkString(",")
        val r = "QK_Rule " + colName + " ValidCheck validvalues" + " [" + validValues + "]"
        println("Valid Check Rule --> " + r)
      }

    }
  }
}