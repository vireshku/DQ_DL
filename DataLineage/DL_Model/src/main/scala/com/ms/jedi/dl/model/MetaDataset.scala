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
  * The case class represents a data set descriptor
  * @param id An unique identifier
  * @param schema A data set schema
  */
case class MetaDataset(id: UUID, schema: Schema)
