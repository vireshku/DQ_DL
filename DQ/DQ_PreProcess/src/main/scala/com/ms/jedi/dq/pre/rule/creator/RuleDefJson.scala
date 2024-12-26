package com.ms.jedi.dq.pre.rule.creator

import org.apache.spark.sql.expressions.MutableAggregationBuffer
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import com.ms.jedi.dq.pre.rule.valueobject.RulePopulate
import com.ms.jedi.dq.pre.rule.model.Rules
import com.ms.jedi.dq.pre.rule.model.ReadControlFile
import com.ms.jedi.dq.pre.dao.RuleDefDao
import scala.collection.mutable.ListBuffer

class RuleDefJson extends UserDefinedAggregateFunction {

  // This is the input fields for your aggregate function.
  override def inputSchema: org.apache.spark.sql.types.StructType =
    StructType(Seq(StructField("RuleName", StringType), StructField("EntityName", StringType), StructField("ColumnName", StringType), StructField("Category", StringType), StructField("Source", StringType),
      StructField("ControlFilePath", StringType), StructField("SubjectArea", StringType), StructField("action", StringType)))

  // This is the internal fields you keep for computing your aggregate.
  override def bufferSchema: StructType =
    {
      StructType(Seq(StructField("RuleName", StringType), StructField("EntityName", StringType), StructField("ColumnName", StringType),
        StructField("Category", StringType), StructField("Source", StringType), StructField("ControlFilePath", StringType), StructField("SubjectArea", StringType), StructField("action", StringType)))
    }

  // This is the output type of your aggregatation function.
  override def dataType: DataType = StringType

  override def deterministic: Boolean = true

  // This is the initial value for your buffer schema.
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = ""
    buffer(1) = ""
    buffer(2) = ""
    buffer(3) = ""
    buffer(4) = ""
    buffer(5) = ""
    buffer(6) = ""
    buffer(7) = ""
  }

  // This is how to update your buffer schema given an input.
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer(0) = input.apply(0).toString()
    buffer(1) = input.apply(1).toString()
    buffer(2) = input.apply(2).toString()
    buffer(3) = input.apply(3).toString()
    buffer(4) = input.apply(4).toString()
    buffer(5) = buffer.getAs[String](5) + RulePopulate.populateRuleAutoOiginal(input.apply(0).toString(),input.apply(1).toString(),input.apply(2).toString(),input.apply(3).toString(),input.apply(4).toString(),input.apply(5).toString(),input.apply(6).toString(),input.apply(7).toString()).toString()+"&"
    println("Update buffer(5) --> " + buffer(5))
    buffer(6) = input.apply(6).toString()

  }

  // This is how to merge two objects with the bufferSchema type.
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getAs[String](0) + buffer2.getAs[String](0)
    buffer1(1) = buffer1.getAs[String](1) + buffer2.getAs[String](1)
    buffer1(2) = buffer1.getAs[String](2) + buffer2.getAs[String](2)
    buffer1(3) = buffer1.getAs[String](3) + buffer2.getAs[String](3)
    buffer1(4) = buffer1.getAs[String](4) + buffer2.getAs[String](4)
    buffer1(5) = buffer1.getAs[String](5)+  buffer2.getAs[String](5)
    buffer1(6) = buffer1.getAs[String](6)+  buffer2.getAs[String](6)
   }

  // This is where you output the final value, given the final value of your bufferSchema.
  override def evaluate(buffer: Row): Any = {
    val ruleName = buffer.apply(0).toString()
    val entity = buffer.getAs[String](1)
    val column = buffer.apply(2).toString()
    val category = buffer.apply(3).toString()
    val source = buffer.apply(4).toString()
    val rules = buffer.apply(5).toString()
    
    
    
   // rlist+=controlFile
    
    // val controlFileObj = ReadControlFile.read(controlFile)
     //  val rules = RulePopulate.populateRuleAuto(ruleName, entity, column, category)
   //  val rd = RulePopulate.populateRuleValControlAuto(controlFileObj, rules.toList)
    //  RuleDefDao.createRulesAuto(rules.toList)
    //  val reuleDef = RuleDefDao.createRuleDefAuto(rd)
    println(rules.toString())
  //  println("rlist(0)===> " + rlist(0))
    //println("rlist(1)===> " + rlist(1))
//    rlist.toString()
    rules

  }

}