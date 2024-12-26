package com.ms.jedi.dq.exec.rule.reader

import com.ms.db.util.KeyValParser

trait QueryBuilder {

  def dbQuery(): String = {
    val DATABASE_ID = KeyValParser.kv().getProperty("ruledb")
    "SELECT * FROM root r WHERE r.id='" + DATABASE_ID + "'"
  }

  def colQuery(): String = {
    val COLLECTION_ID = KeyValParser.kv().getProperty("ruledefcol")
    "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID + "'"
  }

  def docQuery(source: String, entity: String): String = {
    "SELECT c.RuleDefId FROM root c JOIN a in c.MetaData where a.Entity.eName= '" + entity + "'  AND a.Source= '" + source + "' order by c._ts DESC"
  }

  def docQuerybyId(id: String): String = {
    "SELECT * FROM c where c.RuleDefId='" + id + "'"
  }
}