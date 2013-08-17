package lesst

import org.scalatest.FunSpec

class CompileSpec extends FunSpec with Fixtures {
  describe ("compile") {
    it ("should compile basic less files") {
      val path = "less/basic.less"
      val code = file("/" + path)
      Compile(path, code) match {
        case Right(CompilationResult(css, imports)) =>
          assert(css === file("/css/basic.css"))
          assert(imports === List())          
        case Left(f) => fail("expected success but was %s" format f)
      }
    }
  }
}
