package com.ms.jedi.dq.post.rule.PostProcessor

import com.ms.jedi.dq.post.rule.delta.RuleDeltaIdentifier
import com.ms.jedi.dq.post.rule.watermark.RuleWaterMark
import com.ms.jedi.dq.post.rule.delegator.RuleMetricDelegator
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.jedi.dq.post.rule.delta.RuleDeltaScan

trait RulePostProcessorTrait extends RuleDeltaIdentifier with RuleWaterMark with RuleDeltaScan {

  def postProcess(source: String) = {
    println("Starting the DQ Rule Post Processing--------------- ")
    System.setProperty("hadoop.home.dir", "C:\\spark\\bin");
    val wmDebut = waterMarkDebut(source)
    val wnFin = waterMarkEnd(source, wmDebut)
    val tobeProcessed = delta(source,wmDebut, wnFin)
    if (!tobeProcessed.isEmpty) {
      RuleMetricDelegator.delegate(source,tobeProcessed.toMap)
    }
    updateWaterMark(source, wnFin)
  }
}