

package com.ms.jedi.dl.common

import com.ms.jedi.dl.common.TypeFreaks.`not a subtype of`

import scala.language.implicitConversions

/**
  * The object contains implicit conversions of types to options.
  */
object OptionImplicits {

  /**
    * The method coverts on type to an [[scala.Option Option]]
    *
    * @param o An instance that will be converted
    * @tparam T A source type
    * @return An option
    */
  implicit def anyToOption[T <: Any : `not a subtype of`[Option[_]]#λ : Manifest](o: T): Option[T] = Option(o)
}
