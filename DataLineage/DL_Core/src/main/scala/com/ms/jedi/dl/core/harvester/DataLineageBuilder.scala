/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.harvester

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark
import org.apache.spark.SparkContext
import org.apache.spark.sql.catalyst.plans.logical._
import org.apache.spark.sql.execution.datasources.LogicalRelation
import org.apache.spark.sql.execution.{LeafExecNode, SparkPlan}
import scalaz.Scalaz._
import com.ms.jedi.dl.core.harvester.DataLineageBuilder._
import com.ms.jedi.dl.model.DataLineage
import com.ms.jedi.dl.coreadapterapi.SaveJDBCCommand
import com.ms.jedi.dl.coreadapterapi.WriteCommand
import com.ms.jedi.dl.coreadapterapi.SaveAsTableCommand
import com.ms.jedi.dl.coreadapterapi.WriteCommandParserFactory


class DataLineageBuilder(logicalPlan: LogicalPlan, executedPlanOpt: Option[SparkPlan], sparkContext: SparkContext)
                        (hadoopConfiguration: Configuration, writeCommandParserFactory: WriteCommandParserFactory) {

  private val componentCreatorFactory: ComponentCreatorFactory = new ComponentCreatorFactory

  private val writeCommandParser = writeCommandParserFactory.writeParser()
  private val clusterUrl: Option[String] = sparkContext.getConf.getOption("spark.master")
  private val tableCommandParser = writeCommandParserFactory.saveAsTableParser(clusterUrl)
  private val jdbcCommandParser = writeCommandParserFactory.jdbcParser()

  def buildLineage(): Option[DataLineage] = {
    println("DataLineageBuilder ------------->>  +  buildLineage")
    val builders = getOperations(logicalPlan)
    val someRootBuilder = builders.lastOption

    val writeIgnored = someRootBuilder match {
      case Some(rootNode:RootNode) => rootNode.ignoreLineageWrite
      case _ => false
    }

    val operations = builders.map(_.build())

    writeIgnored match {
      case true => {
        println("DataLineageBuilder ------------->>  +  buildLineage --- none")
        None
      }
      case false =>
            {
          println("DataLineageBuilder ------------->>  +  buildLineage --- datalineage produced")    
          Some(
          DataLineage(
            sparkContext.applicationId,
            sparkContext.appName,
            System.currentTimeMillis(),
            spark.SPARK_VERSION,
            operations.reverse,
            componentCreatorFactory.metaDatasetConverter.values.reverse,
            componentCreatorFactory.attributeConverter.values,
            componentCreatorFactory.dataTypeConverter.values
          )
        )
        }
    }
  }

    private def getOperations(rootOp: LogicalPlan): Seq[OperationNodeBuilder] = {
      
    def traverseAndCollect(
                            accBuilders: Seq[OperationNodeBuilder],
                            processedEntries: Map[LogicalPlan, OperationNodeBuilder],
                            enqueuedEntries: Seq[(LogicalPlan, OperationNodeBuilder)]
                          ): Seq[OperationNodeBuilder] = {
      enqueuedEntries match {
        case Nil => accBuilders
        case (curOpNode, parentBuilder) +: restEnqueuedEntries =>
          val maybeExistingBuilder = processedEntries.get(curOpNode)
          val curBuilder = maybeExistingBuilder.getOrElse(createOperationBuilder(curOpNode))

          if (parentBuilder != null) parentBuilder += curBuilder

          if (maybeExistingBuilder.isEmpty) {

            val parsers = Array(jdbcCommandParser, writeCommandParser, tableCommandParser)

            val maybePlan: Option[LogicalPlan] = parsers.
              map(_.asWriteCommandIfPossible(curOpNode)).
              collectFirst {
                case Some(wc) => wc.query
              }

            val newNodesToProcess: Seq[LogicalPlan] =
              maybePlan match {
                case Some(q) => Seq(q)
                case None => curOpNode.children
              }

            traverseAndCollect(
              curBuilder +: accBuilders,
              processedEntries + (curOpNode -> curBuilder),
              newNodesToProcess.map(_ -> curBuilder) ++ restEnqueuedEntries)

          } else {
            traverseAndCollect(accBuilders, processedEntries, restEnqueuedEntries)
          }
      }
    }

      traverseAndCollect(Nil, Map.empty, Seq((rootOp, null)))
  }

  private def createOperationBuilder(op: LogicalPlan): OperationNodeBuilder = {
    implicit val ccf: ComponentCreatorFactory = componentCreatorFactory
    op match {
      case j: Join => new JoinNodeBuilder(j)
      case u: Union => new UnionNodeBuilder(u)
      case p: Project => new ProjectionNodeBuilder(p)
      case f: Filter => new FilterNodeBuilder(f)
      case s: Sort => new SortNodeBuilder(s)
      case s: Aggregate => new AggregateNodeBuilder(s)
      case a: SubqueryAlias => new AliasNodeBuilder(a)
      case lr: LogicalRelation => new ReadNodeBuilder(lr) with HDFSAwareBuilder
      case wc if jdbcCommandParser.matches(op) =>
        val (readMetrics: Metrics, writeMetrics: Metrics) = getMetrics()
        val tableCmd = jdbcCommandParser.asWriteCommand(wc).asInstanceOf[SaveJDBCCommand]
        new SaveJDBCCommandNodeBuilder(tableCmd, writeMetrics, readMetrics)
      case wc if writeCommandParser.matches(op) =>
        val (readMetrics: Metrics, writeMetrics: Metrics) = getMetrics()
        val writeCmd = writeCommandParser.asWriteCommand(wc).asInstanceOf[WriteCommand]
        new WriteNodeBuilder(writeCmd, writeMetrics, readMetrics) with HDFSAwareBuilder
      case wc if tableCommandParser.matches(op) =>
        val (readMetrics: Metrics, writeMetrics: Metrics) = getMetrics()
        val tableCmd = tableCommandParser.asWriteCommand(wc).asInstanceOf[SaveAsTableCommand]
        new SaveAsTableNodeBuilder(tableCmd, writeMetrics, readMetrics)
      case x => new GenericNodeBuilder(x)
    }
  }

  private def getMetrics(): (Metrics, Metrics) = {
    executedPlanOpt.
      map(getExecutedReadWriteMetrics).
      getOrElse((Map.empty, Map.empty))
  }

  trait HDFSAwareBuilder extends FSAwareBuilder {
    override protected def getQualifiedPath(path: String): String = {
      val fsPath = new Path(path)
      val fs = FileSystem.get(hadoopConfiguration)
      val absolutePath = fsPath.makeQualified(fs.getUri, fs.getWorkingDirectory)
      absolutePath.toString
    }
  }

}

object DataLineageBuilder {
  private type Metrics = Map[String, Long]

  private def getExecutedReadWriteMetrics(executedPlan: SparkPlan): (Metrics, Metrics) = {
    def getNodeMetrics(node: SparkPlan): Metrics = node.metrics.mapValues(_.value)

    val cumulatedReadMetrics: Metrics = {
      def traverseAndCollect(acc: Metrics, nodes: Seq[SparkPlan]): Metrics = {
        nodes match {
          case Nil => acc
          case (leaf: LeafExecNode) +: queue =>
            traverseAndCollect(acc |+| getNodeMetrics(leaf), queue)
          case (node: SparkPlan) +: queue =>
            traverseAndCollect(acc, node.children ++ queue)
        }
      }

      traverseAndCollect(Map.empty, Seq(executedPlan))
    }

    (cumulatedReadMetrics, getNodeMetrics(executedPlan))
  }
}
