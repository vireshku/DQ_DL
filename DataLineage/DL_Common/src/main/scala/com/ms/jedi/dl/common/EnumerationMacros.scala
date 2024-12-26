package com.ms.jedi.dl.common

/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
 * A macro to produce a TreeSet of all instances of a sealed trait.
 *
 * Based on Travis Brown's and user673551 work:
 * https://stackoverflow.com/a/24705126/96766
 */
object EnumerationMacros {
  
  def sealedInstancesOf[A]: Set[A] = macro sealedInstancesOf_impl[A]

  def sealedInstancesOf_impl[A: c.WeakTypeTag](c: blackbox.Context): c.Expr[Set[A]] = {
    import c.universe._

    val symbol = weakTypeOf[A].typeSymbol

    if (!symbol.isClass || !symbol.asClass.isSealed)
      c.abort(c.enclosingPosition, "Can only enumerate values of a sealed trait or class.")
    else {
      val siblingSubclasses: List[Symbol] =
        scala.util.Try {
          val enclosingModule = c.internal.enclosingOwner.asInstanceOf[ModuleDef]
          enclosingModule.impl.body.filter { x =>
            scala.util.Try(x.symbol.asModule.moduleClass.asClass.baseClasses.contains(symbol)) getOrElse false
          }.map(_.symbol)
        } getOrElse Nil

      val children = symbol.asClass.knownDirectSubclasses.toList ::: siblingSubclasses
      if (!children.forall(x => x.isModuleClass || x.isModule))
        c.abort(c.enclosingPosition, "All children must be objects.")
      else
        c.Expr[Set[A]] {
          val sourceModuleRef = (sym: Symbol) => Ident(
            if (sym.isModule) sym
            else sym.asInstanceOf[scala.reflect.internal.Symbols#Symbol].sourceModule.asInstanceOf[Symbol])

          Apply(
            Select(reify(Set).tree, TermName("apply")),
            children.map(sourceModuleRef))
        }
    }
  }
}