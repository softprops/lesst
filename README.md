# lesst

a chauffeur for scala patrons in the less css compilation party

## /!\ Extraction in progress

This library is the extraction of the less css compiler used
in less-sbt for use as a standalone library


## usage

This library provides a scala interface for compile less css source files into css files

To compile less source code

```scala
lesst.Compile(fileName, lessSourceCode)
```

This returns a ``scala.util.Try[CompilationResult]` which provides access the compiled css
and a list of file imports included in the result.

You can optionally minify the generated css 

```scala
import lesst.{ Compile, Options }
Compile(fileName, lessSourceCode, Options().copy(mini = true))
```

Got other stuff to do? Why wait? Put a `scala.util.Future` on it.

import scala.concurrent.Future
import ExecutionContext.Implicits.global
import lesst.{ Compile, CompilationResult }
Future(Compile(file, lessSourceCode)).map {
  CompilationResult(css, imports) =>
    Thread.sleep(1000)
    println(css)
}
println("compiling scala...")


Doug Tangren (softprops) 2013
