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

case class RuleOutput2(
  ruleId: String,
  result: Int,
  detail: String)
case class RuleExecVersion2(
  version:    Int = 2,
  RuleOutput: List[RuleOutput2])