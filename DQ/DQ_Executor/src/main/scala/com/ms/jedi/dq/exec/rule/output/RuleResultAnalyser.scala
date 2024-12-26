package com.ms.jedi.dq.exec.rule.output

/**
 * Helper class for rule result analysis
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import scala.collection.Seq

trait RuleResultAnalyser {

  def prepareQualified(ruleDefId: String, dataList: List[Dataset[Row]], data: Dataset[Row]): Dataset[Row] = {
    val dqdata = dataList.reduce(_.join(_, Seq("jId")))
    val finalData = data.join(dqdata, Seq("jId"))
    finalData
  }

}