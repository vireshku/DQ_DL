package com.ms.jedi.dq.post.rule.model

import java.sql.Timestamp
import org.apache.spark.sql.Row

case class DQMetricOverAll(
  Source:                String,
  EntityName:            String,
  PassPercentage:        Double,
  FailedPercentage:      Double,
  DQAppliedCreatedDate:  Timestamp,
  DQAppliedModifiedDate: Timestamp)