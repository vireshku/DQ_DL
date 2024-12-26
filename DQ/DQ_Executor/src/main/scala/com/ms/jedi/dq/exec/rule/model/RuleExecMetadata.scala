package com.ms.jedi.dq.exec.rule.model

/**
 * Schema model class for rule output
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

case class RuleOutput(
  ruleId:      String,
  result:      Int,
  category:    String,
  subjectArea: String,
  action:      String,
  rule: String,
  dimension: String
  )
case class RuleExecMetadata(
  version:    Int              = 1,
  RuleOutput: List[RuleOutput])