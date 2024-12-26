/**
 * ADLS connector class ,based on adapter pattern
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */
package com.ms.db.adapter

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SQLContext
import com.ms.db.session.NativeSession
import com.microsoft.azure.datalake.store.ADLStoreClient
import com.ms.db.util.KeyValParser
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

object AdlsAdapter {

  /**
   * Session Adapter for Azure ADLS
   */
  def spark(entity: String): SparkSession = {

    val sparkSession = NativeSession.spark(entity)
    val adlsCredential = KeyValParser.kv.getProperty("adls_credential")
    val adlsId = KeyValParser.kv.getProperty("adls_id")
    val adlsLoginUrl = KeyValParser.kv.getProperty("refreshurl")

    val databrickScope = KeyValParser.kv.getProperty("databricks_scope")
    val decryptedADLSId = dbutils.secrets.get(scope = databrickScope, key = adlsId)
    val decryptedADLSCredential = dbutils.secrets.get(scope = databrickScope, key = adlsCredential)
    sparkSession.conf.set("dfs.adls.oauth2.access.token.provider.type", "ClientCredential")
    sparkSession.conf.set("dfs.adls.oauth2.client.id", s"${decryptedADLSId}")
    sparkSession.conf.set("dfs.adls.oauth2.credential", s"${decryptedADLSCredential}")
    sparkSession.conf.set("dfs.adls.oauth2.refresh.url", adlsLoginUrl)
    sparkSession.conf.set("spark.databricks.delta.preview.enabled", "true")
    sparkSession.conf.set("spark.databricks.delta.merge.joinBasedMerge.enabled", "true")
    sparkSession
  }

  def sqc(entity: String): SQLContext = {
    spark(entity).sqlContext
  }

  def sc(entity: String): SparkContext = {
    spark(entity).sparkContext
  }

  def client: ADLStoreClient = {
    val adlsCredential = KeyValParser.kv.getProperty("adls_credential")
    val adlsId = KeyValParser.kv.getProperty("adls_id")
    val adlsLoginUrl = KeyValParser.kv.getProperty("refreshurl")

    val databrickScope = KeyValParser.kv.getProperty("databricks_scope")
    val decryptedADLSId = dbutils.secrets.get(scope = databrickScope, key = adlsId)
    val decryptedADLSCredential = dbutils.secrets.get(scope = databrickScope, key = adlsCredential)
    val client: ADLStoreClient = ADLStoreClient.createClient(KeyValParser.kv.getProperty("adls"), decryptedADLSCredential);
    client
  }
}