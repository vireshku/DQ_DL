package com.ms.jedi.dq.exec.rule.strategy

/**
 * Algorithm implementation for range check,based on strategy pattern
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

object RuleRangeCheck {

  //  DQ_Rule RangeCheck vwPPGTaxonomyPlan BusinessScenario >= 1
  //  DQ_Rule RangeCheck vwPPGTaxonomyPlan BusinessScenario <= 5
  //  DQ_Rule RangeCheck vwPPGTaxonomyPlan 5 >= BusinessScenario >= 1
  //  DQ_Rule RangeCheck vwPPGTaxonomyPlan BusinessScenario = 10
  //  DQ_Rule RangeCheck vwPPGTaxonomyPlan BusinessScenario != 10
  //  DQ_Rule RangeCheck vwPPGTaxonomyPlan BusinessScenario in (10,20,30)

  def check(id: String, rule: String,d: Dataset[Row]): Dataset[Row] = {
    val entityName = rule.split(" ").apply(2)
    val columns = rule.split(" ").apply(3)
    val operator = rule.split(" ").apply(4)
    val value = rule.split(" ").apply(5)

    println("starting with the data quality range check ......")
    val data = d
    val r = data.withColumn(id.toString(), rangeudf(col(columns), lit(value), lit(operator)))
    println(r.show())
    r.select("jId", id.toString())
  }

  def rangeudf = udf((f: String, value: String, operator: String) => {

    operator match {
      case ">=" => if (f >= value) { 1 } else { 0 }
      case "<=" => if (f <= value) { 1 } else { 0 }
      case "="  => if (f == value) { 1 } else { 0 }
      case "!=" => if (f != value) { 1 } else { 0 }
      case _    => 0

    }
  })

}