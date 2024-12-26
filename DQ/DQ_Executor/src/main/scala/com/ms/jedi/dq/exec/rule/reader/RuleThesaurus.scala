package com.ms.jedi.dq.exec.rule.reader

/**
 * Utility class for preparing data and rules to be passed to the specific executors,based on facade pattern
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._

import com.microsoft.azure.documentdb.Database
import com.microsoft.azure.documentdb.Document
import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.dq.exec.rule.dao.RuleDefDaoFactory
import com.ms.jedi.dq.exec.rule.model._
import com.ms.jedi.dq.exec.rule.writer.RuleResultWriter

import net.liftweb.json.DefaultFormats
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.SaveMode
import com.microsoft.azure.documentdb.FeedOptions
import com.ms.db.util.KeyValParser

object RuleThesaurus extends QueryBuilder {

  def ruleDefbyEntity(source: String, entity: String): RuleDefinition = {
    val options: FeedOptions = new FeedOptions();
    options.setEnableCrossPartitionQuery(true);
    implicit val formats = DefaultFormats
    val documentClient = RuleDefDaoFactory.getDocumentClient();
    val datab = documentClient.queryDatabases(dbQuery(), options).getQueryIterable.toList().get(0)
    val colList = documentClient.queryCollections(datab.getSelfLink, colQuery(), options).getQueryIterable.toList().get(0)
    val id = documentClient.queryDocuments(colList.getSelfLink, docQuery(source, entity), options).getQueryIterable.toList().get(0).get("RuleDefId").toString()
    println("RuleDefId :::-> " + id)
    val ruleDef = documentClient.queryDocuments(colList.getSelfLink, docQuerybyId(id), options).getQueryIterable.toList().get(0)
    val ruledefinition = net.liftweb.json.parse(ruleDef.toString())
    val ruleObj = ruledefinition.extract[RuleDefinition]
    ruleObj
  }

  def getRuleMetaDatabyId(source: String, entity: String, ruleDefId: String): List[Rules] = {
    ruleDefbyEntity(source, entity).Rules
  }
}