package com.ms.jedi.dq.post.rule.model

case class DQRuleAudit(

  source: String, entity: String, filePath: String, watermark: String)
