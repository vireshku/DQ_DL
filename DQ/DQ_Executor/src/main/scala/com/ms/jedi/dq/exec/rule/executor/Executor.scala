package com.ms.jedi.dq.exec.rule.executor

/**
 * This class is rule executor heirarchy parent
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.jedi.dq.exec.rule.model.Rules
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

trait Executor {
  
  def execute(rule:Rules,d:Dataset[Row]):Dataset[Row]
  
}