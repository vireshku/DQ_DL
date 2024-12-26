package com.ms.jedi.dq.post.rule.model

case class DQPostMetadata(
  val source:     String,
  val entityName: String,
  val filePath:   String,
  val waterMarkEndVal: String)