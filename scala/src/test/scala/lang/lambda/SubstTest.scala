package lang.lambda

import org.scalatest._

import lang.lambda._

/**
 * Created by seba on 01/08/14.
 */
class SubstTest extends FunSuite {

  val p1 = Lam("x", Var("x"))

  test ("p1") {
    assertResult(p1)(p1.unsafeSubst("x", Var("y")))
    assertResult(p1)(p1.unsafeSubst("y", Var("y")))
  }

  val p2 = Lam("x", Var("y"))
  test ("p2_capture") {
    assertResult(p2)(p2.unsafeSubst("x", Var("z")))
    assertResult(Lam("x", Var("z")))(p2.unsafeSubst("y", Var("z")))
  }

  test ("p2_safe") {
    assertResult(Lam("x_0", Var("x")))(p2.subst("y", Var("x")))
  }
}
