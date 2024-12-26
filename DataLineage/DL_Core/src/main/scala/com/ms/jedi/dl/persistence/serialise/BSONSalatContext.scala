
package com.ms.jedi.dl.persistence.serialise

import salat.Context

class BSONSalatContext extends salat.Context with CommonSalatContext {
  override val name = "BSON Salat context"
  registerGlobalKeyOverride("id", "_id")
}

object BSONSalatContext {
  private val ctx_with_fix_for_SL_126: Context = new BSONSalatContext {
    override val name = "BSON Salat Context with fix for SL-126"
    registerCustomTransformer(new AggregateOperationTransformer_SL126)
  }

  implicit val ctx: Context = ctx_with_fix_for_SL_126
}
