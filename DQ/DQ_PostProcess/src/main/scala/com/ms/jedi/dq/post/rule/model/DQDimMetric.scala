package com.ms.jedi.dq.post.rule.model

import java.sql.Timestamp

case class DQDimMetric(

  Source:                String,
  EntityName:            String,
  PassPercentage:        Double,
  FailedPercentage:      Double,
  DQAppliedCreatedDate:  Timestamp,
  DQAppliedModifiedDate: Timestamp)