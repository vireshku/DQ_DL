package com.ms.jedi.dq.post.rule.delegator

import com.ms.jedi.dq.post.rule.metrics.OverAllDQPerYear
import com.ms.jedi.dq.post.rule.metrics.OverAllDQPerDay
import com.ms.jedi.dq.post.rule.metrics.OverAllDQPerQuarter
import com.ms.jedi.dq.post.rule.metrics.OverAllDQPerWeek

object RuleMetricsHandler {

  def handle(source: String) = {
    println("Started with the static metric population------------")
    
    OverAllDQPerWeek.metrics(source)
    OverAllDQPerQuarter.metrics(source)
    OverAllDQPerYear.metrics(source)
    OverAllDQPerDay.metrics(source)
    println("Finished with the static metric population------------")
  }
}