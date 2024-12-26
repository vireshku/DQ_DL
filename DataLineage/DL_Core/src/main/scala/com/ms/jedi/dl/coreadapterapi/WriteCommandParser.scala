/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.coreadapterapi

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.catalyst.plans.logical.{Command, LogicalPlan}

import scala.language.implicitConversions
import scala.reflect.ClassTag


abstract class WriteCommandParser[T <: LogicalPlan](implicit tag: ClassTag[T]) {
  def matches(operation: LogicalPlan): Boolean

  def asWriteCommand(operation: T): AbstractWriteCommand

  def asWriteCommandIfPossible(operation: T): Option[AbstractWriteCommand] =
    if (matches(operation)) Some(asWriteCommand(operation))
    else None
}

abstract class WriteCommandParserFactory {
  def writeParser(): WriteCommandParser[LogicalPlan]
  def saveAsTableParser(clusterUrl: Option[String]) : WriteCommandParser[LogicalPlan]
  def jdbcParser(): WriteCommandParser[LogicalPlan]
}

object WriteCommandParserFactory extends AdapterFactory[WriteCommandParserFactory]

abstract class AbstractWriteCommand extends Command {
val query: LogicalPlan
}

case class WriteCommand(path:String, mode: SaveMode, format: String, query: LogicalPlan) extends AbstractWriteCommand

case class SaveAsTableCommand(tableName:String, mode: SaveMode, format: String, query: LogicalPlan) extends AbstractWriteCommand

object URIPrefixes {
  //prefix used in identifiers for saveAsTable writes
  val managedTablePrefix = "table://"
  val jdbcTablePrefix = "jdbc://"
}

case class SaveJDBCCommand(tableName:String, mode: SaveMode, format: String, query: LogicalPlan) extends AbstractWriteCommand