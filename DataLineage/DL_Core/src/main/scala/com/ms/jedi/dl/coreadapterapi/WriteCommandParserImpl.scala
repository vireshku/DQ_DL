
package com.ms.jedi.dl.coreadapterapi

import org.apache.commons.codec.net.URLCodec
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.execution.command.CreateDataSourceTableAsSelectCommand
import org.apache.spark.sql.execution.datasources.jdbc.JdbcRelationProvider
import org.apache.spark.sql.execution.datasources.{ InsertIntoHadoopFsRelationCommand, SaveIntoDataSourceCommand }

class WriteCommandParserFactoryImpl extends WriteCommandParserFactory {
  override def writeParser(): WriteCommandParser[LogicalPlan] = new WriteCommandParserImpl()

  override def saveAsTableParser(clusterUrl: Option[String]): WriteCommandParser[LogicalPlan] = new SaveAsTableCommandParserImpl(clusterUrl)

  override def jdbcParser(): WriteCommandParser[LogicalPlan] = new SaveJdbcCommandParserImpl()
}

class SaveAsTableCommandParserImpl(clusterUrl: Option[String]) extends WriteCommandParser[LogicalPlan] {
  override def matches(operation: LogicalPlan): Boolean = operation.isInstanceOf[CreateDataSourceTableAsSelectCommand]

  override def asWriteCommand(operation: LogicalPlan): AbstractWriteCommand = {
    val op = operation.asInstanceOf[CreateDataSourceTableAsSelectCommand]

    val identifier = op.table.storage.locationUri match {
      case Some(location) =>
        {
          println("CreateDataSourceTableAsSelectCommand------------ location -----> " + location.toURL.toString)
          location.toURL.toString
        }
      case _ =>
        {
          println("CreateDataSourceTableAsSelectCommand------------ No location -----> ")
          val codec = new URLCodec()
          URIPrefixes.managedTablePrefix +
            codec.encode(clusterUrl.getOrElse(throw new IllegalArgumentException("Unknown cluster name."))) + ":" +
            codec.encode(op.table.identifier.database.getOrElse("default")) + ":" +
            codec.encode(op.table.identifier.table)
        }
    }

    SaveAsTableCommand(identifier, op.mode, "table", op.query)
  }
}

class SaveJdbcCommandParserImpl extends WriteCommandParser[LogicalPlan] {
  override def matches(operation: LogicalPlan): Boolean = {
    operation.isInstanceOf[SaveIntoDataSourceCommand] &&
      operation.asInstanceOf[SaveIntoDataSourceCommand].dataSource.isInstanceOf[JdbcRelationProvider]
  }

  override def asWriteCommand(operation: LogicalPlan): AbstractWriteCommand = {
    operation match {
      case op: SaveIntoDataSourceCommand =>
        val url = op.options.getOrElse("url", throw new NoSuchElementException("Cannot get name of JDBC connection string."))
        val table = op.options.getOrElse("dbtable", throw new NoSuchElementException("Cannot get name of JDBC table."))
        val identifier = s"${URIPrefixes.jdbcTablePrefix}$url:$table"
        SaveJDBCCommand(identifier, op.mode, "jdbc", op.query)
    }
  }
}

class WriteCommandParserImpl extends WriteCommandParser[LogicalPlan] {

  override def matches(operation: LogicalPlan): Boolean = {
    val result = operation.isInstanceOf[InsertIntoHadoopFsRelationCommand]
    println("WriteCommandParserImpl ---------->  " + result)
    result
  }
  override def asWriteCommand(operation: LogicalPlan): WriteCommand = {
    val op = operation.asInstanceOf[InsertIntoHadoopFsRelationCommand]
    println("asWriteCommand ---------------> " + op.query.toString())
    WriteCommand(op.outputPath.toString, op.mode, op.fileFormat.toString, op.query)
  }
}