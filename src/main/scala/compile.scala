package lesst

import org.mozilla.javascript.{
  Callable, Context, Function, FunctionObject, JavaScriptException,
  NativeArray, NativeObject, Scriptable, ScriptableObject, ScriptRuntime }
import java.io.InputStreamReader
import java.nio.charset.Charset
import scala.collection.JavaConverters._

case class StyleSheet(src: String, imports: List[String])

object Compile {
  type Result = Either[CompilationError, StyleSheet]
  def apply(filename: String, code: String, options: Options = Options()) =
    DefaultCompile(filename, code, options)
}

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

abstract class AbstractCompile(src: String)
  extends ((String, String, Options) => Compile.Result)
    with ShellEmulation {

  // rhino/less issue: https://github.com/cloudhead/less.js/issues/555

  def apply(name: String, code: String, options: Options): Compile.Result =
    withContext { ctx =>
      val less = scope.get("compile", scope).asInstanceOf[Callable]
      try {
        less.call(ctx, scope, scope, Array(name, code, options.mini.asInstanceOf[AnyRef])) match {
          case sheet: ScriptableStyleSheet => Right(sheet.result)
          case ur => Left(UnexpectedResult(ur))
        }
      } catch {
          case e: JavaScriptException =>
            e.getValue match {
              case v: Scriptable =>
                // fixme: kind of a janky solution. room for impovement here
                val errorInfo = (Map.empty[String, Any] /: LessError.Properties)(
                  (a,e) =>
                    if (v.has(e, v)) v.get(e, v) match {
                      case null =>
                        a
                      case na: NativeArray =>
                        a + (e -> na.toArray.map(_.asInstanceOf[Any]).toSeq)
                      case dbl: java.lang.Double
                      if (Seq("line","column", "index").contains(e)) =>
                        a + (e -> dbl.toInt)
                      case job =>
                        a + (e -> job.asInstanceOf[Any])
                    } else a
                )
                Left(LessError.from(options.colors, errorInfo))
              case ue =>
                Left(UnexpectedError(ue)) // null, undefined, Boolean, Number, String, or Function
            }
        }
  }

  override def toString = "%s (%s)" format(super.toString, src)

  private val utf8 = Charset.forName("utf-8")

  private lazy val scope = withContext { ctx =>
    val scope = emulated(ctx.initStandardObjects())
    ctx.evaluateReader(
      scope,
      new InputStreamReader(
        getClass().getResourceAsStream("/%s" format src),
        utf8),
      src, 1, null
    )
    ScriptableObject.defineClass(scope, classOf[ScriptableStyleSheet]);
    scope
  }

  private def withContext[T](f: Context => T): T = {
    val ctx = Context.enter()
    try {
      // Do not compile to byte code (max 64kb methods)
      ctx.setOptimizationLevel(-1)
      f(ctx)
    } finally {
      Context.exit()
    }
  }
}

object DefaultCompile extends AbstractCompile("less-rhino-1.4.2.js")
