package macromisc

import scala.language.experimental.macros

object MacroImplementationPlayground {

  def update[A, B](model: A)(selector: A => B)(updater: B => B): A = macro updateImpl[A, B]

  import scala.reflect.macros.blackbox.Context

  def updateImpl[A, B](c: Context)(model: c.Expr[A])(selector: c.Expr[A => B])(updater: c.Expr[B => B]): c.Tree = {

    import c.universe._

      // _c.e.v => List(c,e,v)
      def disassemble(body: c.Tree): List[c.TermName] =
        body match {

          case q"$parent.$child" =>
            disassemble(parent) :+ child

          case _ =>
            Nil
        }

      // List(c, e, v) => _.c.e.v
      def reassemble(tokens: List[c.TermName]): c.Tree =
        tokens.foldLeft(q"(x => x)") {
          case (q"($args) => $body", t) =>
            q"($args) => $body.$t"
        }

    selector.tree match {

      case q"($args) => $body" =>
        disassemble(body) match {

          case first :: rest =>
            q"$model.copy($first = update($model.$first)(${reassemble(rest)})($updater))"

          case _ =>
            q"$updater($model)"
        }
    }
  }

}