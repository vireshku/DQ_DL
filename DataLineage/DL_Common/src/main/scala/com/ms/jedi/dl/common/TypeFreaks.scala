package com.ms.jedi.dl.common

object TypeFreaks {

  // Encoding for "A is not a subtype of B"
  trait !<:[A, B]

  type `not a subtype of`[T] = {type λ[U] = U !<: T} //NOSONAR

  // use ambiguous method declarations to rule out excluding type conditions
  implicit def passingProbe[A, B]: A !<: B = null //NOSONAR

  implicit def failingProbe1[A, B >: A]: A !<: B = null //NOSONAR

  implicit def failingProbe2[A, B >: A]: A !<: B = null //NOSONAR
}
