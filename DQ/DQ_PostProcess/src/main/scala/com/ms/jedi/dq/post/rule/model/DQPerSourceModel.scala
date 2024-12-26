package com.ms.jedi.dq.post.rule.model

import java.sql.Timestamp

case class DQPerSourceModel(

  Source:                String,
  PassPercentage:        Double,
  FailedPercentage:      Double,
  DQAppliedCreatedDate:  Timestamp,
  DQAppliedModifiedDate: Timestamp)