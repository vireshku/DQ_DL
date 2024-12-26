package com.ms.jedi.dq.exec.rule.reader

import scala.io.Source
import com.ms.jedi.dq.exec.rule.model.vResult
import net.liftweb.json.DefaultFormats
import com.ms.jedi.dq.exec.rule.model.RuleExecMetadata
import com.ms.jedi.dq.exec.rule.model.RuleExecVersion
import com.ms.jedi.dq.exec.rule.model.RuleExecVersion2
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

object JsonParser {

  def parse = {
    implicit val formats = DefaultFormats
    val jString = Source.fromFile("C:\\Users\\virkumar\\Desktop\\vJson.json")
    val ruledefinition = net.liftweb.json.parse(jString.mkString)
    val ruleObj = ruledefinition.extract[vResult]
    println(ruleObj.version)

    if (ruleObj.version.equals("1")) {
      val ruleObj22 = ruledefinition.extract[RuleExecVersion]
      ruleObj22.RuleOutput.foreach {
        r =>
          println(r.ruleId + " ----- " + r.result + " ------ " + r.detail)
      }
    }
  }
  
  def parseJMD(jString: String) = {
    implicit val formats = DefaultFormats
    val ruledefinition = net.liftweb.json.parse(jString)
    val ruleObj = ruledefinition.extract[vResult]
    println(ruleObj.version)

    if (ruleObj.version.equals("1")) {
      val ruleObj22 = ruledefinition.extract[RuleExecVersion]
      ruleObj22.RuleOutput.foreach {
        r =>
          println(r.ruleId + " ----- " + r.result + " ------ " + r.detail)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    parse
  }
}