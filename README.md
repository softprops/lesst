# lesst

[![Build Status](https://travis-ci.org/softprops/lesst.png?branch=master)](https://travis-ci.org/softprops/lesst)

a chauffeur for [scala](http://www.scala-lang.org/) patrons in the [less css](http://lesscss.org/) compilation party.

## install

Via the copy and paste method

```scala
resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

libraryDependencies += "me.lessis" %% "lesst" % "0.1.2"
```

Via [a more civilized method](https://github.com/softprops/ls#readme) which will do the same without all the manual work.

    > ls-install lesst
        
_Note_ If you are a [bintray-sbt](https://github.com/softprops/bintray-sbt#readme) user you can optionally specify the resolver as
            
```scala
resolvers += bintray.Opts.resolver.repo("softprops", "maven")
```

## usage

This library provides a scala interface for compiling beautiful less css source files into slightly less beauitful css files, lest you
actually like repeating your self over and over in css. In which case, this library may not be for you.

### Input sources

To compile less source code, you apply a `lesst.InputSource[T]` to the `lesst.Compile` object. There are currently two out-of-the-box `InputSources` available which are implicitly resolved.

You can provide a fileName and less source code.

```scala
lesst.Compile()(fileName, lessSourceCode)
```

A filename is required to resolve relative paths to less `@imports`, otherwise they won't work.

You can also provide a `java.net.URL` representing the .less file resource.

```scala
lesst.Compile()(getClass().getResource(fileName))
```

An `InputSource` is a simple type class defined as 

```scala
trait InputSource[T] {
  def filename: String
  def src: String
}
```

Compile will implicitly resolve an instance of this for type `T` when compiling less sources.

### Compile options

You can optionally minify the generated css if you like "skinny" output. The default is to not minify output.

```scala
import lesst.{ Compile, Options }
Compile(Options(mini = true))(fileName, lessSourceCode)
```

You can also store a reference to Compiler an call method which return a new compile with updated options

```scala
lesst.Compile().minify(true)(fileName, lessSource)
```

### Compilation results

The compilation results in a `scala.Either[CompilationError, StyleSheet]` which provides access the compiled css
and a list of file imports included in the StyleSheet or a CompilationError containing information about what happened and where.

### Error reporting

Unless you are [Chuck Norris](http://darcxed.wordpress.com/2012/03/19/the-ultimate-top-30-chuck-norris-the-programmer-jokes/), you probably will stumble on a syntax error on occasion when writing less css. Lesst keeps that in mind and provides _good_ error reporting with formatted `CompilationErrors`.

![errors](errors.png)

### Future is full of choices

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
