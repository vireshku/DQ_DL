package com.ms.jedi.dq.pre.dao

/**
 * Data Access Object to access the Azure Cosmos Data Base
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.google.gson.Gson
import com.ms.jedi.dq.pre.dao.factory.RuleDefDaoFactory
import com.microsoft.azure.documentdb.Database
import com.microsoft.azure.documentdb.DocumentCollection
import com.microsoft.azure.documentdb.Document
import com.ms.jedi.dq.pre.rule.model.RuleDefinition
import com.microsoft.azure.documentdb.DocumentClientException
import net.liftweb.json.DefaultFormats
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import scala.collection.mutable.ListBuffer
import com.ms.jedi.dq.pre.rule.model.MetaData
import com.ms.jedi.dq.pre.rule.model.Columns
import com.ms.jedi.dq.pre.rule.model.Rules
import com.ms.jedi.dq.pre.rule.valueobject.RulePopulate
import com.ms.jedi.dq.pre.rule.result.RuleExecMetadata
import com.ms.jedi.dq.pre.rule.model.Rules
import com.ms.jedi.dq.pre.rule.model.InputRules
import com.microsoft.azure.documentdb.FeedOptions

object RuleDefDao {

  def createRuleDefAuto(rd: RuleDefinition): RuleDefinition = {
    val DATABASE_ID = "DataQualityDB";
    val COLLECTION_ID = "RuleDef";
    val documentClient = RuleDefDaoFactory.getDocumentClient();
    implicit val formats = DefaultFormats
    val jsonString = write(rd)
    val ruledefDocument: Document = new Document(jsonString)
    val datab = documentClient.queryDatabases("SELECT * FROM root r WHERE r.id='" + DATABASE_ID + "'", null).getQueryIterable.toList().get(0)
    val colList = documentClient.queryCollections(datab.getSelfLink, "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID + "'", null).getQueryIterable.toList().get(0)
    var savedRuleDefDoc: Document = null
    try {
      savedRuleDefDoc = documentClient.createDocument(colList.getSelfLink, ruledefDocument, null, false).getResource();
      println("Rule definition document saved......................")
    } catch {
      case e: DocumentClientException => println(" Error while saving the rule definition document ==> " + e.getMessage)
      case e: Exception               => println(" Error while saving the rule definition document ==> " + e.getMessage)
    }

    println("Created Rule Def Json ---> " + savedRuleDefDoc.toString())
    val parsedJson = net.liftweb.json.parse(savedRuleDefDoc.toString())
    val jj = parsedJson.extract[RuleDefinition]
    return jj
  }

  def createRuleDef(): RuleDefinition = {
    val DATABASE_ID = "DataQualityDB";
    val COLLECTION_ID = "RuleDef";
    val documentClient = RuleDefDaoFactory.getDocumentClient();
    val databaseCache: Database = null
    val collectionCache: DocumentCollection = null
    implicit val formats = DefaultFormats
    val jsonString = write("")
    var todoItemDocument: Document = new Document(jsonString)
    val datab = documentClient.queryDatabases("SELECT * FROM root r WHERE r.id='" + DATABASE_ID + "'", null).getQueryIterable.toList().get(0)
    val colList = documentClient.queryCollections(datab.getSelfLink, "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID + "'", null).getQueryIterable.toList().get(0)
    try {
      // Persist the document using the DocumentClient.
      todoItemDocument = documentClient.createDocument(
        colList.getSelfLink, todoItemDocument, null,
        false).getResource();
      println("")
    } catch {
      case e: DocumentClientException => throw e

    }

    val parsedJson = net.liftweb.json.parse(todoItemDocument.toString())
    val jj = parsedJson.extract[RuleDefinition]
    /*  val docList = documentClient.queryDocuments(colList.getSelfLink, "SELECT * FROM root r ", null).getQueryIterable.toList().get(0)
    val ruledefinition = net.liftweb.json.parse(docList.toString())
    val ruleObj = ruledefinition.extract[RuleDefinition]
    println(ruleObj.RuleDefId)*/

    val options: FeedOptions = new FeedOptions();
    options.setEnableCrossPartitionQuery(true);
    val entity = "ActivityPointerBase"
    // val colList = documentClient.queryCollections(datab.getSelfLink, "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID + "'", null).getQueryIterable.toList().get(0)
    val docList = documentClient.queryDocuments(colList.getSelfLink, "SELECT c.RuleDefId FROM root c JOIN a in c.MetaData where a.Entity.eName= '" + entity + "' order by c._ts DESC", options)
      .getQueryIterable.toList().get(0)
    val id = docList.get("RuleDefId").toString()
    println("RuleDefId :::-> " + id)

    return jj
  }

  def createRulesAuto(rules: List[Rules]): List[Rules] = {
    val DATABASE_ID = "DataQualityDB";
    val COLLECTION_ID = "Rules";
    val documentClient = RuleDefDaoFactory.getDocumentClient();
    val databaseCache: Database = null
    val collectionCache: DocumentCollection = null

    implicit val formats = DefaultFormats
    val xxx = InputRules.apply(rules)
    val jsonString = write(xxx)

    var todoItemDocument: Document = new Document(jsonString)
    val datab = documentClient.queryDatabases("SELECT * FROM root r WHERE r.id='" + DATABASE_ID + "'", null).getQueryIterable.toList().get(0)

    val colList = documentClient.queryCollections(datab.getSelfLink, "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID + "'", null).getQueryIterable.toList().get(0)

    try {
      // Persist the document using the DocumentClient.
      todoItemDocument = documentClient.createDocument(
        colList.getSelfLink, todoItemDocument, null,
        false).getResource();
      println("")
    } catch {
      case e: DocumentClientException => null
    }

    val parsedJson = net.liftweb.json.parse(todoItemDocument.toString())

    val jj = parsedJson.extract[InputRules]
    println(jj.Rules.apply(0).rule)
    return jj.Rules
  }

  def createRules(): Rules = {
    val DATABASE_ID = "DataQualityDB";
    val COLLECTION_ID = "Rules";
    val documentClient = RuleDefDaoFactory.getDocumentClient();
    // Cache for the database object, so we don't have to query for it to
    // retrieve self links.
    val databaseCache: Database = null
    // Cache for the collection object, so we don't have to query for it to
    // retrieve self links.
    val collectionCache: DocumentCollection = null

    implicit val formats = DefaultFormats
    val jsonString = write("")
    val options: FeedOptions = new FeedOptions();
    options.setEnableCrossPartitionQuery(true);

    var todoItemDocument: Document = new Document(jsonString)
    val datab = documentClient.queryDatabases("SELECT * FROM root r WHERE r.id='" + DATABASE_ID + "'", options).getQueryIterable.toList().get(0)

    val colList = documentClient.queryCollections(datab.getSelfLink, "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID + "'", options).getQueryIterable.toList().get(0)

    try {
      // Persist the document using the DocumentClient.
      todoItemDocument = documentClient.createDocument(
        colList.getSelfLink, todoItemDocument, null,
        false).getResource();
      println("")
    } catch {
      case e: DocumentClientException => null
    }

    val parsedJson = net.liftweb.json.parse(todoItemDocument.toString())

    val jj = parsedJson.extract[Rules]
    //println(jj.L1.mkString("------"))
    return jj
  }

  def createResult(): RuleExecMetadata = {
    val DATABASE_ID = "TestDB";
    val COLLECTION_ID = "TestCollection";
    val documentClient = RuleDefDaoFactory.getDocumentClient();
    // Cache for the database object, so we don't have to query for it to
    // retrieve self links.
    val databaseCache: Database = null
    // Cache for the collection object, so we don't have to query for it to
    // retrieve self links.
    val collectionCache: DocumentCollection = null

    implicit val formats = DefaultFormats
    val jsonString = write("")

    var todoItemDocument: Document = new Document(jsonString)
    val datab = documentClient.queryDatabases("SELECT * FROM root r WHERE r.id='" + DATABASE_ID + "'", null).getQueryIterable.toList().get(0)

    val colList = documentClient.queryCollections(datab.getSelfLink, "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID + "'", null).getQueryIterable.toList().get(0)

    try {
      // Persist the document using the DocumentClient.
      todoItemDocument = documentClient.createDocument(
        colList.getSelfLink, todoItemDocument, null,
        false).getResource();
      println("")
    } catch {
      case e: DocumentClientException => null
    }

    val parsedJson = net.liftweb.json.parse(todoItemDocument.toString())

    val jj = parsedJson.extract[RuleExecMetadata]
    // println(jj.L1.mkString("------"))
    return jj
  }

  def main(args: Array[String]): Unit = {

    createRuleDef()
    // createRules
    //    createResult()
  }

}