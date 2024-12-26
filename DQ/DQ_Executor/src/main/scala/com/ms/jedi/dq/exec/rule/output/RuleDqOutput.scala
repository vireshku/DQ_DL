package com.ms.jedi.dq.exec.rule.output

/**
 * Helper class for rule execution and result collation
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import com.ms.jedi.dq.exec.rule.model._
import com.ms.jedi.dq.exec.rule.reader.RuleThesaurus
import org.apache.spark.sql.Dataset

object RuleDqOutput extends RuleDataQual with RuleResultAnalyser {

  def resultJson(source: String, entity: String, ruleDefId: String, dataList: List[Dataset[Row]], d: Dataset[Row]): Dataset[Row] = {

    val finalData = prepareQualified(ruleDefId, dataList, d)
    println(finalData.show())
    val rList = RuleThesaurus.getRuleMetaDatabyId(source,entity,ruleDefId)
    println("starting with the final data quality result metadata creation ......")
    val taggedData = finalData.withColumn("DQ_Result", dataQualification(rList.toSeq)(struct(finalData.columns.map(col): _*))).drop(rList.map(_.ruleId): _*).drop("jId")
    println(taggedData.show())
    //Send taggedData to the post processor
    taggedData
  }
}