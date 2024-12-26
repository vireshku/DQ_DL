package com.ms.jedi.dq.pre.rule.result

case class RuleOutput(
  ruleId: Double,
  result: Int,
  detail: String
)
case class RuleExecMetadata(
  RuleOutput: List[RuleOutput]
)

//version info