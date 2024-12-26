package com.ms.jedi.dq.exec.rule.agg

/**
 * UDAF for rule results aggregator for all L1,L2,L3
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 *
 */


import org.apache.spark.sql.expressions.UserDefinedAggregateFunction
import org.apache.spark.sql.types.LongType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.expressions.MutableAggregationBuffer
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType

object RuleResultAgg extends UserDefinedAggregateFunction {
  
  
  // Input Data Type Schema
  def inputSchema: StructType = StructType(Array(StructField("item", StringType)))
 
  // Intermediate Schema
  def bufferSchema = StructType(Array(
    StructField("sum", DoubleType),
    StructField("cnt", LongType)
  ))
 
  // Returned Data Type .
  def dataType: DataType = StringType
 
  // Self-explaining
  def deterministic = true
 
  // This function is called whenever key changes
  def initialize(buffer: MutableAggregationBuffer) = {
    buffer(0) = 0.toDouble // set sum to zero
    buffer(1) = 0L // set number of items to 0
  }
 
  // Iterate over each entry of a group
  def update(buffer: MutableAggregationBuffer, input: Row) = {
    buffer(0) = buffer.getDouble(0) + input.getDouble(0)
    buffer(1) = buffer.getLong(1) + 1
  }
 
  // Merge two partial aggregates
  def merge(buffer1: MutableAggregationBuffer, buffer2: Row) = {
    buffer1(0) = buffer1.getDouble(0) + buffer2.getDouble(0)
    buffer1(1) = buffer1.getLong(1) + buffer2.getLong(1)
  }
 
  // Called after all the entries are exhausted.
  def evaluate(buffer: Row) = {
    buffer.getDouble(0)/buffer.getLong(1).toDouble
  }
  
  
}