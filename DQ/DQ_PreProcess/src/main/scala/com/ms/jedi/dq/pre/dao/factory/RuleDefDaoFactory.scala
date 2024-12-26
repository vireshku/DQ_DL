package com.ms.jedi.dq.pre.dao.factory

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
import com.microsoft.azure.documentdb.PartitionResolver
import com.ms.db.util.KeyValParser
import com.ms.db.util.KeyValParser
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

object RuleDefDaoFactory {

  var documentClient: DocumentClient = null
  val HOST = "https://psbidevdqcosmosdb.documents.azure.com:443/"

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