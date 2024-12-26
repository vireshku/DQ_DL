/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.model


import java.util.UUID

/**
  * Represents a persisted source data (e.g. file)
  *
  * @param path        file location
  * @param datasetsIds IDs of associated dataset(s) that was read/written from/to the given data source
  */
case class MetaDataSource(path: String, datasetsIds: Seq[UUID])

/**
  * Represents a persisted source data (e.g. file).
  * Same as [[MetaDataSource]] but with type
  *
  * @param `type`      source type
  * @param path        file location
  * @param datasetsIds ID of an associated dataset that was read/written from/to the given data source
  */
case class TypedMetaDataSource(`type`: String, path: String, datasetsIds: Seq[UUID])
