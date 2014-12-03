package lang.lambda

import name.NameFix._
import name.NameGraph
import name.{Name, Nominal}

/**
 * Created by seba on 01/08/14.
 */
abstract class Exp extends Nominal {
  type Scope = Map[String,Name.ID]
  type ModularScope = Map[(String, String),(Name.ID, Name.ID)]

  def resolveNames: NameGraph = resolveNames(Map())
  def resolveNames(scope: Scope): NameGraph = resolveNames(scope, Map())
  def resolveNames(scope: Scope, modularScope: ModularScope): NameGraph
  def rename(renaming: Renaming): Exp
  def replaceByQualifiedVar(name: Name, qualifiedVar: QualifiedVar): Exp

  def unsafeSubst(x: String, e: Exp): Exp
  def subst(w: String, e: Exp): Exp = nameFix(resolveNames, unsafeSubst(w, e))

  def unsafeNormalize: Exp
  def normalize = nameFix(resolveNames, unsafeNormalize)

  def alphaEqual(e: Exp): Boolean = {
    val gthis = resolveNames
    val ge = e.resolveNames
    alphaEqual(e, gthis ++ ge)
  }
  def alphaEqual(e: Exp, g: NameGraph): Boolean
}