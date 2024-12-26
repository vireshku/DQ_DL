package com.ms.jedi.dq.pre.rule.creator

/**
 * Generator class for creating rules through the excel
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.functions._

import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.pre.dao.RuleDefDao
import com.ms.jedi.dq.pre.rule.valueobject.RulePopulate

object RuleCreator {

  def create() = {
    System.setProperty("hadoop.home.dir", "C:\\spark\\bin");
    val data = AdlsAdapter.spark("Pre-Process").read.
      format("com.crealytics.spark.excel")
      .option("location", "adl://psinsightsadlsdev01.azuredatalakestore.net/PPE/DQ-Rule/RuleDefiner.xlsx")
      .option("useHeader", "true")
      .option("treatEmptyValuesAsNulls", "true")
      .option("inferSchema", "true")
      .option("addColorColumns", "False")
      .load()
    data.show(false)
    val RuleDefAgg = new RuleDefJson
    val res = data.groupBy(col("EntityName")).agg(RuleDefAgg(col("RuleName"), col("EntityName"), col("ColumnName"), col("Category"), col("Source"), col("ControlFilePath"), col("SubjectArea"), col("action")).as("Rules"))
    println(res.show())
    res.collect().foreach { input =>
      val entityNAme = input.getString(0)
      val computeInput = input.getString(1)
      val rd = RulePopulate.populateComputeRules(computeInput,entityNAme)
      val reuleDef = RuleDefDao.createRuleDefAuto(rd)
    }
    println("Rule creation finished.....................")
  }

  def main(args: Array[String]): Unit = {
    create()
  }

}
