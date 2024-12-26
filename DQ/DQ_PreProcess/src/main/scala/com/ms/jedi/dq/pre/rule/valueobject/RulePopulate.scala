package com.ms.jedi.dq.pre.rule.valueobject

import com.ms.jedi.dq.pre.rule.model.RuleDefinition
import com.ms.jedi.dq.pre.rule.model.Columns

import com.ms.jedi.dq.pre.rule.model.MetaData

import com.ms.jedi.dq.pre.rule.model.InputRules
import com.ms.jedi.dq.pre.rule.model.Rules
import com.ms.jedi.dq.pre.rule.result.RuleExecMetadata
import com.ms.jedi.dq.pre.rule.result.RuleOutput
import com.ms.jedi.dq.pre.rule.model.Entity
import java.util.UUID
import com.ms.jedi.dq.pre.rule.model.ReadControlFile
import com.ms.jedi.dq.pre.rule.model.RuleControlFile2
import scala.collection.mutable.ListBuffer

object RulePopulate {

  def populateMetadata(controlFileObj: RuleControlFile2, source: String, controlFile: String, entityName: String): MetaData = {
    val entity = Entity.apply(entityName, true)
    val ColumnList = ListBuffer[Columns]()
    controlFileObj.Table.FieldList.foreach {
      f =>
        ColumnList += Columns.apply(f.FieldName, f.FieldDataType)
    }
    println(" controlFile path ====> " + controlFile)
    MetaData.apply(source, entity, controlFile, ColumnList.toList)
  }

  def populateComputeRules(rules: String, entityName: String): RuleDefinition = {
    var rr = new ListBuffer[Rules]
    var controlFile = ""
    var source = ""
    rules.split("&").foreach { d =>
      val aggRule = d.split(",")
      val r = aggRule(0)
      val ruleId = aggRule(1)
      val category = aggRule(2)
      val entity = aggRule(3)
      val column = aggRule(4)
      source = aggRule(5)
      controlFile = aggRule(6)
      val subArea = aggRule(7)
      val action = aggRule(8)
      val rulename = aggRule(9)
      println("RuleName ----> " + rulename)

      rulename match {

        case "NullCheck"  => rr += Rules.apply(r, ruleId, category, entity, column, subArea, action, "Completeness")
        case "Unique"     => rr += Rules.apply(r, ruleId, category, entity, column, subArea, action, "Uniqueness")
        case "ValidCheck" => rr += Rules.apply(r, ruleId, category, entity, column, subArea, action, "Validity")
        case "EmptyCheck" => rr += Rules.apply(r, ruleId, category, entity, column, subArea, action, "Accuracy")
      }
    }

    val metaDataList = new ListBuffer[MetaData]
    val controlFileObj = ReadControlFile.read(controlFile)
    val md = populateMetadata(controlFileObj, source, controlFile, entityName)
    metaDataList += md
    val rd = RuleDefinition.apply(UUID.randomUUID().toString(), metaDataList.toList, rr.toList)
    rd
  }

  def populateRuleAutoOiginal(rName: String, eName: String, cName: String, category: String, source: String, controlFile: String, subArea: String, action: String): String = {
    val rr1 = "DQ_Rule " + rName + " " + eName + " " + cName
    val r1 = rr1 + "," + java.util.UUID.randomUUID().toString() + "," + category + "," + eName + "," + cName + "," + source + "," + controlFile + "," + subArea + "," + action + "," + rName
    println("r1 ---->" + r1)
    r1
  }
}