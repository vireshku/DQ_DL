package com.ms.jedi.dq.exec.rule.router

/**
 * Utility class for the ule to the specific executor based on categories executors,based on delegate pattern
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.jedi.dq.exec.rule.model._
import com.ms.jedi.dq.exec.rule.executor.L1RuleExecutor
import com.ms.jedi.dq.exec.rule.executor.Executor
import com.ms.jedi.dq.exec.rule.executor.L2RuleExecutor
import com.ms.jedi.dq.exec.rule.executor.L3RuleExecutor
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.jedi.dq.exec.rule.output.RuleDqOutput
import org.apache.spark.sql.Dataset
import com.ms.jedi.dq.exec.rule.writer.RuleResultWriter

object RuleRouter {

  def route(source: String, entity: String, ruleDefId: String, rules: List[Rules], dataBatch: Map[String, Dataset[Row]]) = {
    var executor: Executor = null
    dataBatch.map({ d =>
      val DqDataList = ListBuffer[Dataset[Row]]()
      rules.map({
        rule =>
          rule.category match {
            case "L1" => executor = L1RuleExecutor
            case "L2" => executor = L2RuleExecutor
            case "L3" => executor = L3RuleExecutor
          }
          DqDataList += executor.execute(rule, d._2)
      })

      RuleResultWriter.write(d._1, RuleDqOutput.resultJson(source, entity, ruleDefId, DqDataList.toList, d._2))
    })
  }
}