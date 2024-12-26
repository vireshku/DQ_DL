package com.ms.jedi.observer.mount

import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

import com.ms.db.util.KeyValParser

object LakeMount {

  def doMount(region: String, env: String) = {
    println("do mount  started --------------------------------->")
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
      "dfs.adls.oauth2.refresh.url" -> adlsLoginUrl)
    val base = KeyValParser.kv().getProperty("aadls")
    val lakePath = base.concat("/" + env + "/" + region)
    if (!dbutils.fs.mounts.map(mnt => mnt.mountPoint).contains("/mnt/observer"+env+"/" + region + "/"))
      dbutils.fs.mount(
        source = lakePath,
        mountPoint = s"/mnt/observer"+env+"/" + region + "/",
        extraConfigs = configs)
    println("do mount performed succesfully ---------------> for /mnt/observer"+env+"/" + region)
  }
}