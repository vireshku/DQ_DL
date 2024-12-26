package com.ms.jedi.dq.post.rule.delta

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

import scala.collection.mutable.Seq
import scala.collection.immutable.List
import scala.collection.mutable.ListMap
import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.Dataset
import com.ms.db.util.KeyValParser
import com.ms.jedi.dq.post.rule.model.DQRuleAudit

trait RuleDeltaScan {

  def getDeltaScan(dataSource: String, timestamp: Long): (Array[DQRuleAudit], Long) = {

    var auditcollection = Array[DQRuleAudit]()
    val lakePath = KeyValParser.kv().getProperty("aadls").concat("/PPE/Gold")

    println("Started with getDeltaScan-------------------------->")
    val basePath = "/mnt/dq/Gold/".concat(dataSource)
    val fileType = ".parquet"
    var files = Array[String]()
    var maxTS: Long = 0
    val conf = new Configuration()
    val hdfs = FileSystem.get(conf)
    val f = new Path(basePath)
    val messageFile = hdfs.listFiles(f, true)

    while (messageFile.hasNext()) {
      val message = messageFile.next()
      if ((message.getPath.toString().endsWith(".parquet") || message.getPath.toString().endsWith(".tsv")) && !(message.getPath.toString().contains("part-"))) {
        if (message.getModificationTime > timestamp) {
          val file = message.getPath.toString()
          files = files :+ file
          val splittedFile = file.replace("dbfs:/mnt/dq/Gold/", "").split("/")
          val filePath = lakePath.concat(file.replace("dbfs:/mnt/dq/Gold", ""))
          if (message.getModificationTime > maxTS) {
            maxTS = message.getModificationTime
            auditcollection = auditcollection :+ DQRuleAudit.apply(splittedFile.apply(0), splittedFile.apply(1), filePath, maxTS.toString())
          }
        }
      }
    }
    println("getDeltaScan scan file list size -----> " + auditcollection.size)
    println("getDeltaScan scan file nax TS -----> " + maxTS)
    return (auditcollection, maxTS)
  }

  def doMount(dataSource: String) = {
    println("do mount  started for source--------------------------------->" + dataSource)
    val adlsCredential = KeyValParser.kv.getProperty("adls_credential")
    val adlsId = KeyValParser.kv.getProperty("adls_id")
    val adlsLoginUrl = KeyValParser.kv.getProperty("refreshurl")

    val databrickScope = KeyValParser.kv.getProperty("databricks_scope")
    val decryptedADLSId = dbutils.secrets.get(scope = databrickScope, key = adlsId)
    val decryptedADLSCredential = dbutils.secrets.get(scope = databrickScope, key = adlsCredential)
    val configs = Map(
      "dfs.adls.oauth2.access.token.provider.type" -> "ClientCredential",
      "dfs.adls.oauth2.client.id" -> decryptedADLSId,
      "dfs.adls.oauth2.credential" -> decryptedADLSCredential,
      "dfs.adls.oauth2.refresh.url" -> "https://login.microsoftonline.com/72f988bf-86f1-41af-91ab-2d7cd011db47/oauth2/token")
    val lakePath = KeyValParser.kv().getProperty("aadls").concat("/PPE/Gold/" + dataSource)
    if (!dbutils.fs.mounts.map(mnt => mnt.mountPoint).contains("/mnt/dq/Gold/".concat(dataSource)))
      dbutils.fs.mount(
        source = lakePath,
        mountPoint = s"/mnt/dq/Gold/".concat(dataSource),
        extraConfigs = configs)
    println("do mount performed succesfully ---------------> for /mnt/dq/Gold/" + dataSource)
  }

  def doMountOrg(dataSource: String) = {
    println("do mount  started for source--------------------------------->" + dataSource)
    val adlsCredential = KeyValParser.kv.getProperty("adls_credential")
    val adlsId = KeyValParser.kv.getProperty("adls_id")
    val adlsLoginUrl = KeyValParser.kv.getProperty("refreshurl")

    val databrickScope = KeyValParser.kv.getProperty("databricks_scope")
    val decryptedADLSId = dbutils.secrets.get(scope = databrickScope, key = adlsId)
    val decryptedADLSCredential = dbutils.secrets.get(scope = databrickScope, key = adlsCredential)
    val configs = Map(
      "dfs.adls.oauth2.access.token.provider.type" -> "ClientCredential",
      "dfs.adls.oauth2.client.id" -> decryptedADLSId,
      "dfs.adls.oauth2.credential" -> decryptedADLSCredential,
      "dfs.adls.oauth2.refresh.url" -> "https://login.microsoftonline.com/72f988bf-86f1-41af-91ab-2d7cd011db47/oauth2/token")
    val lakePath = KeyValParser.kv().getProperty("aadls").concat("/PPE/Gold/" + dataSource)
    println("lakePath --> " + lakePath)
    if (!dbutils.fs.mounts.map(mnt => mnt.mountPoint).contains("/mnt/dq1/Gold/" + dataSource))
      dbutils.fs.mount(
        source = lakePath,
        mountPoint = s"/mnt/dq1/Gold/" + dataSource,
        extraConfigs = configs)
    println("do mount performed succesfully ---------------> for /mnt/dq1/Gold/" + dataSource)
  }

  def getDeltaScanOrg(dataSource: String, timestamp: Long): (Array[DQRuleAudit], Long) = {

    var auditcollection = Array[DQRuleAudit]()
    val lakePath = KeyValParser.kv().getProperty("aadls").concat("/PPE/Gold")

    println("Started with getDeltaScan-------------------------->" + dataSource)
    val basePath = "/mnt/dq1/Gold/" + dataSource
    println("basePath ==" + basePath)
    val fileType = ".parquet"
    var files = Array[String]()
    var maxTS: Long = 0
    val conf = new Configuration()
    val hdfs = FileSystem.get(conf)
    val f = new Path(basePath)
    val messageFile = hdfs.listFiles(f, true)

    while (messageFile.hasNext()) {
      val message = messageFile.next()
      if ((message.getPath.toString().endsWith(".parquet") || message.getPath.toString().endsWith(".tsv")) && !(message.getPath.toString().contains("part-"))) {
        if (message.getModificationTime > timestamp) {
          val file = message.getPath.toString()
          files = files :+ file
          val splittedFile = file.replace("dbfs:/mnt/dq1/Gold/", "").split("/")
          val filePath = lakePath.concat(file.replace("dbfs:/mnt/dq1/Gold", ""))
          println("filePath ==> " + filePath)
          if (message.getModificationTime > maxTS) {
            maxTS = message.getModificationTime

          }
          auditcollection = auditcollection :+ DQRuleAudit.apply(splittedFile.apply(0), splittedFile.apply(1), filePath, message.getModificationTime.toString())

        }
      }
    }
    println("getDeltaScan scan file list size -----> " + auditcollection.size)
    println("getDeltaScan scan file nax TS -----> " + maxTS)
    return (auditcollection, maxTS)
  }

}