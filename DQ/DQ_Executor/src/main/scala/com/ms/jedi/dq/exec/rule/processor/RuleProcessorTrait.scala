package com.ms.jedi.dq.exec.rule.processor

import com.ms.jedi.dq.exec.rule.audit.RuleDeltaIdentifier
import com.ms.jedi.dq.exec.rule.audit.RuleWaterMark
import com.ms.jedi.dq.exec.rule.router.RuleRouter
import com.ms.jedi.dq.exec.rule.reader.RuleThesaurus
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.ms.jedi.dq.exec.rule.audit.RuleContolFile

trait RuleProcessorTrait extends RuleDeltaIdentifier with RuleWaterMark with RuleContolFile {

  def read(source: String, entity: String) = {
    println("Starting the DQ Rule processing for the source and  entity :::: --------------- " + source + "     <------->    " + entity)
    val wmDebut = waterMarkDebut(source, entity)
    val wmFin = waterMarkEnd(source, entity, wmDebut)
    val dataBatch = delta(source, entity, wmDebut, wmFin)

    if (!dataBatch.isEmpty) {
      val ruleDef = RuleThesaurus.ruleDefbyEntity(source, entity)
      println("Executing the rule definition with  the rule def id as ===> " + ruleDef.RuleDefId)
      RuleRouter.route(source, entity, ruleDef.RuleDefId, ruleDef.Rules, dataBatch)
      updateWaterMark(source, entity, wmFin)
    }
    val fileInfo = controlFile(source, entity)
    dbutils.fs.cp(fileInfo.from, fileInfo.to, true)
  }
}