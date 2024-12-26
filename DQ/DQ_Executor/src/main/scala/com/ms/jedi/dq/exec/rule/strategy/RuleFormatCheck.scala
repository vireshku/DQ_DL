package com.ms.jedi.dq.exec.rule.strategy

/**
 * Algorithm implementation for format check,based on strategy pattern
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

object RuleFormatCheck {

  // DQ_Rule FormatCheck vwPPGTaxonomyPlan BusinessEmail cccc@ccccc.cccc
  // DQ_Rule FormatCheck vwPPGTaxonomyPlan OppurtunityId nnnnnnnnnnnnnn

  def check(id: String, rule: String): Dataset[Row] = {

    null
  }
}