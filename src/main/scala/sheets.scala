package lesst

import org.mozilla.javascript.{ NativeArray, ScriptableObject }

case class StyleSheet(src: String, imports: List[String])

class ScriptableStyleSheet extends ScriptableObject {
  implicit class NativeArrayWrapper(arr: NativeArray) {
    def toList[T](f: AnyRef => T): List[T] =
      (arr.getIds map { id: AnyRef =>
        f(arr.get(id.asInstanceOf[java.lang.Integer], null))
      }).toList
  }
  // yes. it's mutable. it's also javascript.
  var result: StyleSheet = null

  override def getClassName() = "StyleSheet"

  def jsConstructor(css: String, imports: NativeArray) {
    result = StyleSheet(css, imports.toList(_.toString))
  }
}
