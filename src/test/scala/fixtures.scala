package lesst

import io.Source

trait Fixtures {
  def file(path: String) =
    Option(getClass.getResource(path)).map({ url =>
      Source.fromURL(url).getLines().mkString("\n")
    }).getOrElse(sys.error("%s does not exist" format path))
}
