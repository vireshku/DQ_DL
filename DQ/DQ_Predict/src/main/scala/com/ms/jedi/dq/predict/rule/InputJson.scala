package com.ms.jedi.dq.predict.rule

case class InputJson(

  JobId:    Long,
  DataSets: List[String],
  MetaData: Map[String,String],
  Rules:    Rule,
  Config:   Config)

case class Rule(

  BuisnessRule:    List[String],
  StatisticalRule: List[String],
  PredictedRules:  List[String])

case class Config(

  NullParams:   Map[String, Double],
  ValidParams:  Map[String, Double],
  UniqueParams: Map[String, Double],
  RangeParams:  Map[String, String])