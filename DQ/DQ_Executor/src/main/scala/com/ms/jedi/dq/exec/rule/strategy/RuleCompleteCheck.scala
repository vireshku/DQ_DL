package com.ms.jedi.dq.exec.rule.strategy

/**
 * Algorithm implementation for completeness check,based on strategy pattern
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

object RuleCompleteCheck {

  // DQ_Rule CompleteCheck if vwPPGTaxonomyPlan BusinessScenario [male] Then Domain [Human]

  def check(id: String, rule: String,d: Dataset[Row]): Dataset[Row] = {

    null
  }
}