package com.ms.jedi.dq.pre.rule.model

/**
 * Json parser
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 *
 */

import scala.io.Source
import net.liftweb.json._

import net.liftweb.json.JsonParser._

object JsonReader {

  /* implicit val formats = DefaultFormats

  val jsonFile = Source.fromFile("C:\\Users\\virkumar\\Desktop\\ControlFile\\RuleDefinition.json")
  val parsedJson = net.liftweb.json.parse(jsonFile.mkString)

  val jj = parsedJson.extract[RuleDefinition]

  println(jj.JobId);*/

  def input(): RuleDefinition = {

    implicit val formats = DefaultFormats
    val jsonFile = Source.fromFile("C:\\Users\\virkumar\\Desktop\\ControlFile\\RuleDefinition.json")
    val parsedJson = net.liftweb.json.parse(jsonFile.mkString)
    val input = parsedJson.extract[RuleDefinition]
    println(input.RuleDefId)
    return input

  }

  def main(args: Array[String]): Unit = {
    input()
  }

}