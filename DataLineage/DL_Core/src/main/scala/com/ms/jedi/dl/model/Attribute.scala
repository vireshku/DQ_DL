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
 * The case class represents an attribute of a Spark data set.
 *
 * @param id         An unique identifier of the attribute
 * @param name       A name of the attribute
 * @param dataTypeId A data type of the attribute
 */
case class Attribute(id: UUID, name: String, dataTypeId: UUID)


