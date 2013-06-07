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

A filename is required to resolve relative paths to less imports.

This returns a ``scala.Either[CompilationError, CompilationResult]` which provides access the compiled css
and a list of file imports included in the result.

You can optionally minify the generated css 

```scala
import lesst.{ Compile, Options }
Compile(fileName, lessSourceCode, Options().copy(mini = true))
```

Got other stuff to do? Why wait? Put a `scala.concurrent.Future` on it.


```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lesst.{ Compile, CompilationResult }
Future(Compile(file, lessSourceCode)).map {
  _.fold(println, { _ match {
    case CompilationResult(css, imports) =>
      Thread.sleep(1000)
      println(css)
  }})
}
println("compiling scala...")
```

Doug Tangren (softprops) 2013
