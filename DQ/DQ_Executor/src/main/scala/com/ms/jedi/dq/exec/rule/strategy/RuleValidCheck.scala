package com.ms.jedi.dq.exec.rule.strategy

/**
 * Algorithm implementation for validation check,based on strategy pattern
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
import org.apache.spark.broadcast.Broadcast
import com.ms.db.adapter.AdlsAdapter

object RuleValidCheck {

  // DQ_Rule ValidCheck vwPPGTaxonomyPlan BusinessScenario [Fans Engagement,Media Solution Dev]

  def check(id: String, rule: String, d: Dataset[Row]): Dataset[Row] = {
    val entityName = rule.split(" ").apply(2)
    val columns = rule.split(" ").apply(3)
    val tempSplit = rule.split('[').apply(1)
    val values = tempSplit.substring(0, tempSplit.size - 1)
    println("starting with the data quality valid check ......")
    val seed = AdlsAdapter.sc(entityName).broadcast(values.split(","))
    val r = d.withColumn(id.toString(), validudf(seed)(col(columns)))
    r.select("jId", id.toString())
  }

  def validudf(seed: Broadcast[Array[String]]) = udf((f: String) => {
    if (seed.value.contains(f))
      1
    else
      0
  })
}