package com.ms.jedi.dq.exec.rule.dao

/**
 * Data Access Object Factory to access the Azure Cosmos Data Base,This class is based on the Factory pattern for object oriented design and analysis
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.microsoft.azure.documentdb.DocumentClient
import com.microsoft.azure.documentdb.ConsistencyLevel
import com.microsoft.azure.documentdb.ConnectionPolicy
import com.ms.db.util.KeyValParser
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

object RuleDefDaoFactory {

  //val HOST = "https://vireshcosmosdb.documents.azure.com:443/"
  var documentClient: DocumentClient = null

  def getDocumentClient(): DocumentClient = {
    val HOST = KeyValParser.kv.getProperty("cosmosdb")
    val env = KeyValParser.kv().getProperty("env")
    val databrickScope = KeyValParser.kv.getProperty("databricks_scope")
    val MASTER_KEY = dbutils.secrets.get(scope = databrickScope, key = "PSBI" + env + "DQKEYVAULT")
    if (documentClient == null) {
      documentClient = new DocumentClient(HOST, MASTER_KEY,
        ConnectionPolicy.GetDefault(), ConsistencyLevel.Session)
    }
    documentClient
  }
}