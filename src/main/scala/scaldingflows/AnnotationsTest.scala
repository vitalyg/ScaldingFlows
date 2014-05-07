package scaldingflows

import java.lang.reflect.Method

/**
 * User: vgordon
 * Date: 4/21/14
 * Time: 10:24 AM
 */

class Foo() {
  @ScaldingDependencies(dependencies = "java.lang.String.substring")
  def foo(x: Int, y: String) = x.toDouble
}

class Bar(n: Int) {
  @ScaldingDependencies(dependencies = "Foo.foo")
  def goo(x: String, y: Double) = x.toString

  @ScaldingDependencies(dependencies = "goo")
  def hoo(x: String) = x.toDouble
}

object Obj {
  @ScaldingDependencies(dependencies = "java.lang.String.substring")
  def foo(x: Int, y: String) = x.toDouble
}

object AnnotationsTest extends App {

//  println(Flow.fromEntryPoint("scaldingflows.Bar", "hoo"))
//  println(getMethodFromClass(getObjectFromName("scaldingflows.Obj"), "foo"))
  val fj = new FJ(Map("function" -> "foo"))
}
