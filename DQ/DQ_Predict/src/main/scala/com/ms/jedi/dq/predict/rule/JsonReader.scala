package com.ms.jedi.dq.predict.rule

import scala.io.Source
import net.liftweb.json._

import net.liftweb.json.JsonParser._

object JsonReader extends App {

  implicit val formats = DefaultFormats

  val jsonFile = Source.fromFile("c:\\DQ\\inputnew.json")
  val parsedJson = net.liftweb.json.parse(jsonFile.mkString)

  val jj = parsedJson.extract[InputJson]

  println(jj.JobId);
  println(jj.MetaData.get("col1").get);
  println(jj.DataSets.head)
  println(jj.Rules.BuisnessRule.head)
  println(jj.Rules.StatisticalRule.head)
  println(jj.Rules.PredictedRules.head)
  println(jj.Config.NullParams.get("stdDeviationCoefficient").get)
  println(jj.Config.NullParams.get("maxThreshold").get)
  println(jj.Config.UniqueParams.get("uniqueThreshold").get)
  

  def input(): InputJson = {

    implicit val formats = DefaultFormats
    val jsonFile = Source.fromFile("c:\\DQ\\inputnew.json")
    val parsedJson = net.liftweb.json.parse(jsonFile.mkString)
    val input = parsedJson.extract[InputJson]
    return input

  }

}