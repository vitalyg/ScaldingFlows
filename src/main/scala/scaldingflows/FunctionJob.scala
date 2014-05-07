package scaldingflows

/**
 * User: vgordon
 * Date: 4/28/14
 * Time: 11:00 AM
 */
abstract class FunctionJob(args: Map[String, String]) {
  args.get("function") match {
    case Some(funcName) => this
      .getClass
      .getDeclaredMethods
      .find(_.getName == funcName)
      .get
      .invoke(this)
    case None => println("None")
  }
}

class FJ(args: Map[String, String]) extends FunctionJob(args) {
  def foo() = println("foo")
  def goo() = println("goo")
}
