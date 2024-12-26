package com.ms.jedi.dq.post.rule.model

import java.sql.Timestamp

case class DQWaterMark(
  Source:                String,
  lastBatchRunTime:      String,
  DQAppliedCreatedDate:  Timestamp,
  DQAppliedModifiedDate: Timestamp)