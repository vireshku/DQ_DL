/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.persistence.impl

import org.slf4s.Logging
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.MongoDB
import com.mongodb.casbah.Imports.MongoClientURI

trait MongoConnection {
  def db: MongoDB
}

class MongoConnectionImpl
(
  dbUrl: String,

  dbName: => String = throw new IllegalArgumentException("The connection string must contain a database name")
) extends MongoConnection
  with Logging {

  private val clientUri = MongoClientURI(dbUrl)
  private val client: MongoClient = MongoClient(clientUri)


  val db: MongoDB = {
    val databaseName = clientUri.database getOrElse dbName
    log debug s"Preparing connection: $dbUrl, database = $databaseName"
    val database = client.getDB(databaseName)
    require(database.stats.ok, "database is not OK")
    log debug s"Connected: $dbUrl, database = $databaseName"
    database
  }
}