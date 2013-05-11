package lesst

import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.tools.shell.{ Environment, Global }

object ShellEmulation {
  /** common functions the rhino shell defines */
  val ShellFunctions = Array(
    "doctest",
    "gc",
    "load",
    "loadClass",
    "print",
    "quit",
    "readFile",
    "readUrl",
    "runCommand",
    "seal",
    "sync",
    "toint32",
    "version")
}

/** Most `rhino friendly` js libraries make liberal use
 *  if non emca script properties and functions that the
 *  rhino shell env defines. Unfortunately we are not
 *  evaluating these sources in a rhino shell.
 *  instead of crying me a river, provide an interface
 *  that enables emulation of the shell env */
trait ShellEmulation {

   def emulated(s: ScriptableObject) = {
     // define rhino shell functions
     s.defineFunctionProperties(ShellEmulation.ShellFunctions,
                                classOf[Global],
                                ScriptableObject.DONTENUM)
     // make rhino `detectable`
     // http://github.com/ringo/ringojs/issues/#issue/88

     Environment.defineClass(s)
     s.defineProperty("environment", new Environment(s),
                      ScriptableObject.DONTENUM)
     s
   }
}
