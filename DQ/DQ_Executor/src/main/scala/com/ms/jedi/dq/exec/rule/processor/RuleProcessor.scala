package com.ms.jedi.dq.exec.rule.processor

/**
 * Action executing class for rules and rule definition processing
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

object RuleProcessor extends RuleProcessorTrait {

  def main(args: Array[String]): Unit = {
    read(source = args.apply(0), entity = args.apply(1))
  }
}