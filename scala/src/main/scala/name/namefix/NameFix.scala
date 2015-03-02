package name.namefix

import name.Gensym._
import name._
import name.namegraph.NameGraph.Nodes
import name.namegraph.{NameGraphExtended, NameGraph}

/**
 * Created by seba on 01/08/14.
 */
object NameFix {
  val fixer = new NameFix
  val fixerExtended = new NameFixExtended
  def nameFix[T <: Nominal](gs: NameGraph, t: T): T = fixerExtended.nameFix(gs, t)
  def nameFix[T <: Nominal](gs: NameGraphExtended, t: T): T = fixerExtended.nameFix(gs, t)

}

class NameFix {
  def findCapturedNodes(gs: NameGraph, gt: NameGraph): Nodes = {

    val notPreserveVar = gt.E.filter {
      case (v,d) => gs.V.contains(v) && (gs.E.get(v) match {
        case Some(ds) => d != ds
        case None => v != d
      })
    }

    val notPreserveDef = gt.E.filter {
      case (v,d) => !gs.V.contains(v) && gs.V.contains(d)
    }

    (notPreserveVar ++ notPreserveDef).values.toSet
  }

  def compRenamings(gs: NameGraph, t: Nominal, capture: Nodes): Map[Identifier, Name] = {
    var renaming: Map[Identifier, Name] = Map()
    val newIds = t.allNames -- gs.V

    for (d <- capture) {
      val fresh = gensym(d.name, t.allNames.map(_.name) ++ renaming.values)
      if (gs.V.contains(d)) {
        renaming += (d -> fresh)
        for ((v2,d2) <- gs.E if d == d2)
          renaming += (v2 -> fresh)
      }
      else {
        for (v2 <- newIds if d.name == v2.name)
          renaming += (v2 -> fresh)
      }
    }

    renaming
  }

  def nameFix[T <: Nominal](gs: NameGraph, t: T): T = {
    val gt = t.resolveNames
    val capture = findCapturedNodes(gs, gt)
    if (capture.isEmpty)
      t
    else {
      val renaming = compRenamings(gs, t, capture)
      val tNew = t.rename(renaming).asInstanceOf[T]
      nameFix(gs, tNew)
    }
  }
}