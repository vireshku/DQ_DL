package com.ms.jedi.dq.schema.processor

case class Input1(
  Gender:                  String,
  Rank:                    String,
  NoOfYearInCurrentRank:   String,
  HighestDegree:           String,
  NoOfYearInHighestDegree: String)
case class Inputs(
  input1: List[Input1])
case class GlobalParameters()
case class InputData(
  Inputs:           Inputs,
  GlobalParameters: GlobalParameters)