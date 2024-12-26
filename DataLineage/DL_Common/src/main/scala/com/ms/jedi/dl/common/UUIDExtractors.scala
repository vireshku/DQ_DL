package com.ms.jedi.dl.common


import java.util.UUID

object UUIDExtractors {

  object UUIDExtractor {
    def unapply(str: String): Option[UUID] =
      try Some(UUID fromString str)
      catch {
        case _: IllegalArgumentException => None
      }
  }

}
