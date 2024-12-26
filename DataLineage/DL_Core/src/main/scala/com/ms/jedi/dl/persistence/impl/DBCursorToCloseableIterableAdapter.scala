package com.ms.jedi.dl.persistence.impl

import java.{util => ju}

import com.mongodb.Cursor
import com.mongodb.casbah.Imports._
import com.ms.jedi.dl.persistence.api.CloseableIterable

import scala.collection.JavaConverters._

class DBCursorToCloseableIterableAdapter(cur: Cursor)
  extends CloseableIterable[DBObject](
    iterator = (cur: ju.Iterator[DBObject]).asScala,
    closeFunction = cur.close())
