package com.ms.jedi.dq.exec.rule.output

/**
 * Helper class for L2 rule execution and result collation
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.jedi.dq.exec.rule.model.RuleExecMetadata
import com.ms.jedi.dq.exec.rule.model.RuleOutput
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.Row
import net.liftweb.json.DefaultFormats
import org.apache.spark.sql.functions._
import net.liftweb.json.Serialization.write
import scala.reflect.api.materializeTypeTag
import com.ms.jedi.dq.exec.rule.model.Rules

trait RuleDataQual {

  def dataQualification(ruleList: Seq[Rules]) = udf((row: Row) => {
    val roList = new ListBuffer[RuleOutput]
    implicit val formats = DefaultFormats
    ruleList.foreach {
      r =>
        val rValue = row.getInt(row.fieldIndex(r.ruleId))
        val ro1 = RuleOutput.apply(r.ruleId, rValue, r.category, r.subjectArea, r.action,r.rule,r.dimension)
        roList.append(ro1)
    }
    val r = RuleExecMetadata.apply(1, roList.toList)
    val jsonString = write(r)
    jsonString
  })
}