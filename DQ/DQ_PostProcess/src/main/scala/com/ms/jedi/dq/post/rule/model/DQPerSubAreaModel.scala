package com.ms.jedi.dq.post.rule.model

import java.sql.Timestamp

case class DQPerSubAreaModel(

  SubArea:               String,
  PassPercentage:        Double,
  FailedPercentage:      Double,
  DQAppliedCreatedDate:  Timestamp,
  DQAppliedModifiedDate: Timestamp)