
package com.ms.jedi.dl.persistence.serialise

import java.net.URI

import salat.{BinaryTypeHintStrategy, TypeHintFrequency, TypeHintStrategy}
import salat.transformers.CustomTransformer

/**
  * The trait defines defaults for (de)serialization with Salat library.
  */
trait CommonSalatContext {
  this: salat.Context =>

  override val typeHintStrategy: TypeHintStrategy = BinaryTypeHintStrategy(TypeHintFrequency.WhenNecessary, "_t")

  registerCustomTransformer(new CustomTransformer[URI, String]() {
    override def serialize(uri: URI): String = uri.toString

    override def deserialize(str: String): URI = new URI(str)
  })
}
