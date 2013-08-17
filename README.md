# lesst

a chauffeur for scala patrons in the [less css](http://lesscss.org/) compilation party.

## /!\ Extraction in progress

This library is the extraction of the less css compiler used
in less-sbt for use as a standalone library

## usage

This library provides a scala interface for compiling beautiful less css source files into slightly less beauitful css files, lest you
actually like repeating your self over and over in css. In which case, this library may not be for you.

To compile less source code, apply a filename and a string containing source code to `lesst.Compile`

```scala
lesst.Compile()(fileName, lessSourceCode)
```

A filename is required to resolve relative paths to less @imports.

This returns a `scala.Either[CompilationError, StyleSheet]` which provides access the compiled css
and a list of file imports included in the StyleSheet or a CompilationError containing information about what happened and where.

You can optionally minify the generated css if you like skinny output. The default is to not minify output.

```scala
import lesst.{ Compile, Options }
Compile(Options(mini = true))(fileName, lessSourceCode)
```

You can also store a reference to Compiler an call method which return a new compile with updated options

```scala
lesst.Compile().minify(true)(fileName, lessSource)
```

### future is full of choices

This library does not make choices for you in regard to your choice of asynchronicity. You can make them yourself.

Got other stuff to do? Why wait? Put a `scala.concurrent.Future` on it.

```scala
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lesst.{ Compile, StyleSheet }

Future(Compile()(file, lessSourceCode)).map {
  _.fold(println, {
    case StyleSheet(css, _) =>
      Thread.sleep(1000)
      println(css)
  })
}
println("compiling scala...")
```

## references and notes

* see the [lesscss docs](http://lesscss.org/) for more information on less
* this version of lesst uses a the _1.4.1_  version of the less compiler "under the hood"
* historians will look back on one day realized this code was extracted from [less-sbt](https://github.com/softprops/less-sbt) for the greater good. If you are doing greater good with lesst. You should [let me know about](https://twitter.com/softprops/).
Doug Tangren (softprops) 2013
