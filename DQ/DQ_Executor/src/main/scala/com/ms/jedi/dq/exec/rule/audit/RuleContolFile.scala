package com.ms.jedi.dq.exec.rule.audit

import com.ms.jedi.dq.exec.rule.reader.RuleThesaurus
import com.ms.jedi.dq.exec.rule.model.RuleFileInfo
import com.ms.db.util.KeyValParser

trait RuleContolFile {

  def from(source: String,entity: String): String = {
    var lakePath = KeyValParser.kv().getProperty("aadls")
    val env = KeyValParser.kv().getProperty("env")
    env match {
      case "PROD" => lakePath = lakePath + "/" + RuleThesaurus.ruleDefbyEntity(source,entity).MetaData.apply(0).ControlFile.replace("PPE", "PROD")
      case "PPE"  => lakePath = lakePath + "/" + RuleThesaurus.ruleDefbyEntity(source,entity).MetaData.apply(0).ControlFile
    }
    lakePath
  }

  def to(rawControlFile: String): String = {
    rawControlFile.replace("Raw", "Gold")
  }

  def controlFile(source: String,entity: String): RuleFileInfo = {

    val f = from(source,entity)
    val t = to(f)
    RuleFileInfo.apply(f, t)
  }
}