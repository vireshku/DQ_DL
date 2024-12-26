package com.ms.jedi.dq.exec.rule.executor

/**
 * Current class is based on the delegator pattern for the execution of L2 category rules
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.jedi.dq.exec.rule.model.Rules
import com.ms.jedi.dq.exec.rule.strategy.RuleUniqueCheck
import com.ms.jedi.dq.exec.rule.strategy.RuleNullCheck
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.Dataset
import com.ms.jedi.dq.exec.rule.strategy.RuleValidCheck
import com.ms.jedi.dq.exec.rule.strategy.RuleCompleteCheck
import com.ms.jedi.dq.exec.rule.strategy.RuleRangeCheck
import com.ms.jedi.dq.exec.rule.strategy.RuleEmptyCheck

object L2RuleExecutor extends Executor {

  override def execute(rule: Rules, d: Dataset[Row]): Dataset[Row] = {
    println("Executing L2 rule......with rule id --> " + rule.ruleId)
    println("Rule grammar is --> " + rule.rule)
    val i = rule.ruleId
    var result: Dataset[Row] = null

    rule.rule.split(" ").apply(1) match {
      case "Unique"        => result = RuleUniqueCheck.check(i, rule.rule, d)
      case "NullCheck"     => result = RuleNullCheck.check(i, rule.rule, d)
      case "ValidCheck"    => result = RuleValidCheck.check(i, rule.rule, d)
      case "RangeCheck"    => result = RuleRangeCheck.check(i, rule.rule, d)
      case "CompleteCheck" => result = RuleCompleteCheck.check(i, rule.rule, d)
      case "EmptyCheck"    => result = RuleEmptyCheck.check(i, rule.rule, d)
      case _               => result
    }
    result
  }
}