package com.ms.jedi.dq.post.rule.PostProcessor

import org.apache.spark.sql.catalyst.parser.PostProcessor

object RulePostProcessor extends RulePostProcessorTrait {

  def main(args: Array[String]): Unit = {
    postProcess(source=args.apply(0))
  }
}