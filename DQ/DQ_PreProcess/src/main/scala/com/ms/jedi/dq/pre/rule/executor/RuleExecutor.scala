package com.ms.jedi.dq.pre.rule.executor

import org.apache.spark.sql.functions._
import com.ms.jedi.dq.pre.rule.strategy.RuleNullCheck
import com.ms.jedi.dq.pre.rule.strategy.RuleUniqueCheck
import com.ms.jedi.dq.pre.rule.strategy.RuleDqOutput
import com.ms.jedi.dq.pre.rule.writer.RuleResultWriter

object RuleExecutor {

  /*{
    "MetaData": [
        {
            "Entity": "vwPPGTaxonomyPlan",
            "Columns": [
                {
                    "Column": "CommercialWorkloads",
                    "DataType": "String"
                },
                {
                    "Column": "CommonProduct",
                    "DataType": "String"
                },
                {
                    "Column": "ServicesWorkloads",
                    "DataType": "String"
                }
            ],
            "Source": "Taxonomy"
        },
        {
            "Entity": "vwPPGTaxonomyPlan2",
            "Columns": [
                {
                    "Column": "CommercialWorkloads",
                    "DataType": "String"
                },
                {
                    "Column": "CommonProduct",
                    "DataType": "String"
                },
                {
                    "Column": "ServicesWorkloads",
                    "DataType": "String"
                }
            ],
            "Source": "Taxonomy2"
        }
    ],
    "DataSets": [
        "/PPE/Raw/Taxonomy/dbo_vwPPGTaxonomyPlan/delta/D_vwPPGTaxonomyPlan_v1_2018-07-18-10-30.txt"
    ],

    "Rules": [
        {
            "rule": "DQ_Rule NullCheck BusinessScenario",
            "ruleId": 11,
            "category": "L1"
        },
        {
            "rule": "DQ_Rule Unique BusinessScenario",
            "ruleId": 12,
            "category": "L1"
        },
        {
            "rule": "DQ_Rule Unique Domain",
            "ruleId": 13,
            "category": "L1"
        },
        {
            "rule": "DQ_Rule NullCheck Domain",
            "ruleId": 14,
            "category": "L1"
        }
    ],
    "JobId": 100
}*/

  def main(args: Array[String]): Unit = {
    val dqNull = RuleNullCheck.nullCheck()
    val qualifiedData = RuleUniqueCheck.uniqueCheck(dqNull)
    val taggedData = RuleDqOutput.resultJson(qualifiedData)
    RuleResultWriter.write(taggedData)
  }

}