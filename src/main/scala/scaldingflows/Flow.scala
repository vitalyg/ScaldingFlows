package scaldingflows

import reflect.runtime.universe._
import collection.mutable
import collection.mutable.ListBuffer

/**
 * User: vgordon
 * Date: 4/21/14
 * Time: 2:22 PM
 */
class SignaturesDontMatchException(method: MethodSymbol, dependency: MethodSymbol)
  extends Exception(s"Method ${method.fullName} with parameters [${getMethodParameterTypes(method).mkString(",")}] does not contain a parameter of type ${dependency.returnType}")

object Flow {
  def fromEntryPoint(canonicalClassName: String, functionName: String): Flow = {
    val queue = new mutable.Queue[MethodSymbol]()
    val methods = mutable.Map.empty[MethodSymbol, ListBuffer[MethodSymbol]]
    getMethodFromClass(canonicalClassName, functionName) match {
      case None => Flow(Map())
      case Some(x) => {
        queue.enqueue(x)
        methods(x) = ListBuffer.empty[MethodSymbol]
        while (!queue.isEmpty) {
          val method = queue.dequeue()
          getMethodDependencies(method)
            .flatMap(d => getMethodFromClass(canonicalClassName, d))
            .foreach{ d =>
              if (methodContainsType(method, d.returnType)) {
                methods(method).append(d)
                methods(d) = ListBuffer.empty[MethodSymbol]
                queue.enqueue(d)
              }
              else throw new SignaturesDontMatchException(method, d)
            }
        }
      }
    }
    Flow(methods.mapValues(_.toSeq).toMap)
  }
}
case class Flow(dependencies: Map[MethodSymbol, Seq[MethodSymbol]])
