package lesst

import io.Source

trait Fixtures {
  def url(path: String) =
    getClass.getResource(path)
  def file(path: String) =
    Option(url(path)).map({ url =>
      Source.fromURL(url).getLines().mkString("\n")
    }).getOrElse(sys.error("%s does not exist" format path))
}
