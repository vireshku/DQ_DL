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
  * The case class represents a collection of attributes. See [[com.ms.jedi.dl.model.Attribute Attribute]]
  *
  * @param attrs An internal sequence of attributes (referred by attribute ID)
  */
case class Schema(attrs: Seq[UUID])
