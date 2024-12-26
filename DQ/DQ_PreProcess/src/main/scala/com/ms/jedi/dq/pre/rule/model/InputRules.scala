package com.ms.jedi.dq.pre.rule.model

/**
 * schema model class
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 *
 */

import java.util.UUID

case class Rules(
  rule:        String,
  ruleId:      String,
  category:    String,
  entity:      String,
  column:      String,
  subjectArea: String,
  action:      String,
  dimension:   String)
case class InputRules(
  Rules: List[Rules])