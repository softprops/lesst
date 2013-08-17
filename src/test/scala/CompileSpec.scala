package lesst

import org.scalatest.FunSpec

class CompileSpec extends FunSpec with Fixtures {
  describe ("compile") {
    val compile = Compile()
    it ("should compile basic less files") {
      val path = "less/basic.less"
      val code = file("/" + path)
      compile(path, code) match {
        case Right(sheet) =>
          assert(sheet.src === file("/css/basic.css"))
          assert(sheet.imports === List())
        case Left(f) => fail("expected success but was %s" format f)
      }
    }

    it ("should fail to compile invalid sources with undefined vars") {
      val path = "less/undef.less"
      val code = file("/" + path)
      compile(path, code) match {
        case Right(_) =>
          fail("%s should not have compiled" format path)
        case Left(undefined: UndefinedVar) =>
          assert(undefined.name === "@color")
        case Left(other) =>
          fail("compile resulted in an unexpected error %s"
               format(other))
      }
    }

    it ("should fail to compile with old ~ string interpolation") {
      val path = "less/tilde.less"
      val code = file("/" + path)
      compile(path, code) match {
        case Right(_) =>
          fail("%s should not have compiled")
        case Left(pr: ParseError) =>
          assert(pr.line === 2)
          assert(pr.column === 2)
        case Left(other) =>
          fail("compile resulted in an unexpected error %s"
               format(other))
      }
    }

    it ("should compile with new @{_} interpolation") {
      val path = "less/at.less"
      val code = file("/" + path)
      compile(path, code) match {
        case Right(sheet) =>
          assert(sheet.src === file("/css/at.css"))
        case Left(f) =>
          fail("expected success but was %s" format f)
      }        
    }
  }
}
