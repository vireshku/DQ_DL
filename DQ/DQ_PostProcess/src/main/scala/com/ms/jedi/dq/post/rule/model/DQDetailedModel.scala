package com.ms.jedi.dq.post.rule.model

import java.sql.Timestamp

case class DQDetailedModel(
  Source:                String,
  EntityName:            String,
  SubArea:               String,
  LakePath:              String,
  SampleRecords:         String,
  result:                Double,
  DQAppliedCreatedDate:  Timestamp,
  DQAppliedModifiedDate: Timestamp)