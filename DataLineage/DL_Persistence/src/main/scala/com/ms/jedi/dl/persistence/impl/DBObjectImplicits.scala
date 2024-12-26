package com.ms.jedi.dl.persistence.impl

import com.mongodb.casbah.query.Imports.DBObject

import scala.language.implicitConversions

object DBObjectImplicits {

  implicit class DBObjectWrapper(dbo: DBObject) {
    def putIfAbsent(key: String, value: AnyRef): Unit = {
      if (!dbo.containsField(key))
        dbo.put(key, value)
    }
  }

}