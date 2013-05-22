package lesst

import org.scalatest.FunSpec
import scala.util.Success

class CompileSpec extends FunSpec with Fixtures {
  describe ("compile") {
    it ("should compile basic less files") {
      val path = "less/basic.less"
      val code = file("/" + path)
      Compile(path, code) match {
        case Success(CompilationResult(css, imports)) =>
          assert(css === file("/css/basic.css"))
          assert(imports === List())          
        case f => fail("expected success but was %s" format f)
      }
    }
  }
}
