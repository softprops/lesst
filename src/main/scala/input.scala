package lesst

import java.net.URL
import java.nio.charset.Charset

trait Input[T] {
  def filename: String
  def src: String
}

/** Typeclass interface for compiler input */
@annotation.implicitNotFound(
  msg = "Compiler InputSource[T] type class instance for type ${T} not found")
trait InputSource[T] {
  def apply(t: T): Input[T]
}

object InputSource {
  implicit object FileAndCode extends InputSource[(String, String)] {
    def apply(in: (String, String)) = new Input[(String, String)] {
      val filename = in._1
      val src = in._2
    }
  }

  implicit object Utf8URL extends InputSource[URL] {
    def apply(in: URL) = new Input[URL] {
      val filename = in.getFile
      val src = fromURL(in, Charset.forName("utf-8"))
    }
  }

  implicit object URLAndCharset extends InputSource[(URL, Charset)] {
    def apply(in: (URL, Charset)) = new Input[(URL, Charset)] {
      val filename = in._1.getFile
      val src = fromURL(in._1, in._2)
    }
  }

  private def fromURL(url: URL, charset: Charset) =
    io.Source.fromURL(url)(io.Codec(charset)).mkString
}
