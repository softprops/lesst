package lesst

import org.scalatest.FunSpec

class CompileSpec extends FunSpec with Fixtures {
  describe ("compile") {
    it ("should compile basic less files") {
      val path = "less/basic.less"
      val code = file("/" + path)
      Compile(path, code).fold(fail(_), {
        case CompilationResult(css, imports) =>
          assert(css === file("/css/basic.css"))
          assert(imports === List())
      })
    }
  }
}
