/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.persistence.dao

import com.mongodb.DBObject

import com.mongodb.casbah.query.Implicits.mongoQueryStatements
import com.ms.jedi.dl.persistence.impl.MongoConnection
import com.ms.jedi.dl.persistence.dao.BaselineLineageDAO.Component

class LineageDAOv3(override val connection: MongoConnection) extends BaselineLineageDAO {

  override val version: Int = 3

  override def upgrader: Option[VersionUpgrader] = None

  override protected def getMongoCollectionNameForComponent(component: Component): String = component.name

  override protected val overviewComponentFilter: PartialFunction[Component.SubComponent, DBObject] = {
    case Component.Operation =>
      "_typeHint" $in Seq(
        "com.ms.jedi.dl.model.op.Read",
        "com.ms.jedi.dl.model.op.Write")
  }
}