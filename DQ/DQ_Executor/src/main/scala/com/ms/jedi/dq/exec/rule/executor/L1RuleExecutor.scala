package com.ms.jedi.dq.exec.rule.executor

/**
 * Current class is based on the delegator pattern for the execution of L1 category rules
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.jedi.dq.exec.rule.model.Rules
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

object L1RuleExecutor extends Executor {

  override def execute(rule: Rules, d: Dataset[Row]): Dataset[Row] = {
    println("Executing L1 rule......with rule id --> " + rule.ruleId)
    println("Rule grammar is --> " + rule.rule)

    null
  }

}