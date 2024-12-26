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

import salat.annotations.Salat

/**
 * The trait describes a data type of an attribute, expression, etc.
 */
@Salat
sealed trait DataType {
  val id: UUID
  val nullable: Boolean

  def childDataTypeIds: Seq[UUID]
}

case class Simple(id: UUID, name: String, nullable: Boolean) extends DataType {
  override def childDataTypeIds: Seq[UUID] = Nil
}

object Simple {
  def apply(name: String, nullable: Boolean): Simple = Simple(UUID.randomUUID, name: String, nullable: Boolean)
}

case class Struct(id: UUID, fields: Seq[StructField], nullable: Boolean) extends DataType {
  override def childDataTypeIds: Seq[UUID] = fields.map(_.dataTypeId)
}

object Struct {
  def apply(fields: Seq[StructField], nullable: Boolean): Struct = Struct(UUID.randomUUID, fields, nullable)
}

case class StructField(name: String, dataTypeId: UUID)

case class Array(id: UUID, elementDataTypeId: UUID, nullable: Boolean) extends DataType {
  override def childDataTypeIds: Seq[UUID] = Seq(elementDataTypeId)
}

object Array {
  def apply(elementDataTypeId: UUID, nullable: Boolean): Array = Array(UUID.randomUUID, elementDataTypeId, nullable)
}
