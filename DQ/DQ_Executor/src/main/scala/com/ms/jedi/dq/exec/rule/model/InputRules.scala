package com.ms.jedi.dq.exec.rule.model

/**
 * Schema model class for rules and grammar
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

case class Rules(
  rule:     String,
  ruleId:   String,
  category: String,
  entity:   String,
  column:   String,
  subjectArea: String,
  action: String,
  dimension: String)
case class InputRules(
  Rules: List[Rules])