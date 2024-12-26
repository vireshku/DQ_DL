package com.ms.jedi.dq.post.rule.delegator

import com.ms.jedi.dq.post.rule.writer.RuleMetricsWriter
import com.ms.jedi.dq.post.rule.metrics.OverAllDataQuality
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import com.ms.jedi.dq.post.rule.writer.OverAllDQWriter
import com.ms.jedi.dq.post.rule.metrics.DQPerSubAreaMetrics
import com.ms.jedi.dq.post.rule.metrics.DQPerSourceMetrics
import com.ms.jedi.dq.post.rule.metrics.OverAllDQPerDay
import com.ms.jedi.dq.post.rule.metrics.OverAllDQPerWeek
import com.ms.jedi.dq.post.rule.metrics.OverAllDQPerQuarter
import com.ms.jedi.dq.post.rule.metrics.OverAllDQPerYear
import com.ms.jedi.dq.post.rule.metrics.UniqueDimensionMetric
import com.ms.jedi.dq.post.rule.metrics.CompletenessDimensionMetric
import com.ms.jedi.dq.post.rule.metrics.AccuracyDimensionMetric
import com.ms.jedi.dq.post.rule.metrics.ValidDimensionMetric
import com.ms.jedi.dq.post.rule.metrics.DQDetailedMetric

object RuleMetricDelegator {

  def delegate(source: String, tobeProcessed: Map[DQPostMetadata, Dataset[Row]]) = {
    tobeProcessed.map({
      candidate =>
        OverAllDataQuality.metrics(candidate._1, candidate._2)
        DQPerSubAreaMetrics.metrics(candidate._1, candidate._2)
        DQPerSourceMetrics.metrics(candidate._1, candidate._2)
        UniqueDimensionMetric.metrics(candidate._1, candidate._2)
        CompletenessDimensionMetric.metrics(candidate._1, candidate._2)
        AccuracyDimensionMetric.metrics(candidate._1, candidate._2)
        ValidDimensionMetric.metrics(candidate._1, candidate._2)
        DQDetailedMetric.metrics(candidate._1, candidate._2)
    })
    RuleMetricsHandler.handle(source)
  }
}