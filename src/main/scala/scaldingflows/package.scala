import reflect.runtime.universe._
import util.Try

/**
 * User: vgordon
 * Date: 4/21/14
 * Time: 12:16 PM
 */
package object scaldingflows {
  private val mirror = runtimeMirror(getClass.getClassLoader)

  def getClassFromName(name: String): Option[ClassSymbol] = {
    val clazz = Try(Class.forName(name)).toOption
    clazz.map(c => mirror.classSymbol(c).asClass)
  }
  def getObjectFromName(name: String): ClassSymbol = mirror.staticModule(name).moduleClass.asClass
  def getMethodFromClass(clazz: String, name: String): Option[MethodSymbol] = getClassFromName(clazz).flatMap(c => getMethodFromClass(c, name))
  def getMethodFromClass(clazz: ClassSymbol, name: String): Option[MethodSymbol] = {
    def findMethod(c: ClassSymbol, n: String) = c.toType.members.find(m => m.isMethod && m.name == newTermName(n)).map(_.asMethod)
    def getFullyQualifiedMethod(fullName: String) = {
      val (otherClass, method) = fullName.splitAt(fullName.lastIndexOf('.'))
      getClassFromName(otherClass).flatMap(c => findMethod(c, method.tail))
    }
    def getPackageName(c: ClassSymbol) = mirror.runtimeClass(c).getPackage.getName
    def getLocalMethod = {
      val fullName = s"${getPackageName(clazz)}.$name"
      val localMethod = getFullyQualifiedMethod(fullName)
      if (localMethod.isEmpty)
        throw new NoSuchMethodException(s"Method $name not found in scope")
      else
        localMethod
    }
    findMethod(clazz, name) match {
      case None => getFullyQualifiedMethod(name) match {
          case None => getLocalMethod
          case Some(x) => Some(x)
      }
      case Some(x) => Some(x)
    }
  }
  // currently only supports the first parenthesis of the method
  def getMethodParameterTypes(m: MethodSymbol): List[Type] = m.paramss.head.map(_.typeSignature)
  def methodContainsType(m: MethodSymbol, t: Type) = getMethodParameterTypes(m).find(_ =:= t).isDefined
  def getMethodDependencies(m: MethodSymbol): Seq[String] = {
    def getDependencyValue(a: Annotation) = a.javaArgs(newTermName("dependencies")).asInstanceOf[LiteralArgument].value.value.toString
    val dependency = m.annotations.find(_.tpe == typeOf[ScaldingDependencies])
    dependency
      .toSeq
      .flatMap(getDependencyValue(_).split(',').map(_.trim))
  }
}
