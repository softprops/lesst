package lesst

/** base for all handled compilation errors */
sealed trait CompilationError extends RuntimeException

case class UnexpectedResult(result: Any)
  extends CompilationError {
  override def getMessage =
    "Unexpected javascript return type %s: %s"
       .format(result.getClass, result)
}

case class UnexpectedError(err: Any)
  extends CompilationError {
  override def getMessage =
    "Unexpected error: %s" format err
}

object LessError {
  
  val Col = "column"
  val CallExtract = "callExtract"
  val CallLine = "callLine"
  val Extract = "extract"
  val Filename = "filename"
  val Index = "index"
  val Line = "line"
  val Msg = "message"
  val Name = "name"
  val Stack = "stack"
  val Type = "type"

  trait Formatter[T <: CompilationError] {
    def apply(e: T): String
  }

  trait Formatted[T <: CompilationError] {
    def withFormatter(f: Formatter[T]): T
  }

  val Properties = Seq(
    Name, Msg, Type, Filename, Line,
    Col, CallLine, CallExtract, Stack,
    Extract, Index)

  val UndefVar = """variable (@.*) is undefined""".r

  def from(colors: Boolean, props: Map[String, Any]): CompilationError =
    if (ParseError.is(props)) ParseError(
      props(Line).toString.toInt,
      props(Col).toString.toInt,
      props(Msg).toString,
      props(Extract).asInstanceOf[Seq[String]],
      ParseError.Formatter(colors)
    ) else if (SyntaxError.is(props)) SyntaxError(
      props(Line).toString.toInt,
      props(Col).toString.toInt,
      props(Msg).toString,
      props(Filename).toString,
      props(Extract).asInstanceOf[Seq[String]],
      SyntaxError.Formatter(colors)
    ) else if (props.isDefinedAt(Msg)) {
      UndefVar.findFirstMatchIn(props(Msg).toString).map {
        case matched =>
          UndefinedVar(
            matched.group(1),
            props(Line).toString.toInt,
            props(Col).toString.toInt,
            props(Extract).asInstanceOf[Seq[String]],
            UndefinedVar.Formatter(colors)
          )
      }.getOrElse(GenericLessError(props))
    } else GenericLessError(props)
}

trait Extracts {
  def showExtract(line: Int, col: Int, extract: Seq[String], colors: Boolean = false) =
    (extract.size.toString.size, extract) match {
      case (pad, Seq(null, at, after)) =>
        ("\n %s %" + pad + "d| %s\n   %" + pad + "d| %s").format(
          err(colors,">"), line, err(colors, at), line + 1, after
        )
      case (pad, Seq(before, at, null)) =>
        ("\n  %" + pad + "d| %s\n%s %" + pad + "d| %s").format(
          line - 1, before, err(colors, ">"), line, err(colors, at)
        )
      case (pad, Seq(before, at, after)) =>
        ("\n  %" + pad + "d| %s\n%s %" + pad + "d| %s\n  %" + pad + "d| %s").format(
          line - 1, before, err(colors, ">"), line, err(colors, at), line + 1, after
        )
      case (pad, ext) =>
        ext.mkString("\n | ", " | %\n", "")
    }
  protected def err(colors: Boolean, str: String) =
    if (colors) Console.RED_B + str + Console.RESET else str
}

object SyntaxError {

  case class Formatter(colors: Boolean)
    extends LessError.Formatter[SyntaxError] with Extracts {
    def apply(e: SyntaxError) =
      "Syntax error on line: %s, column: %s in file %s (%s)%s" format(
        e.line, e.column, e.filename, e.message,
        showExtract(e.line, e.column, e.extract, colors))
  }

  def is(props: Map[String, Any]) =
    props.get(LessError.Type).filter(_ == "Syntax").isDefined
}

case class SyntaxError(
  line: Int, column: Int, message: String, filename: String,
  extract: Seq[String], formatter: LessError.Formatter[SyntaxError])
  extends CompilationError with LessError.Formatted[SyntaxError] {
  def withFormatter(f: LessError.Formatter[SyntaxError]) =
    copy(formatter = f)
  override def getMessage = formatter(this)
}

object ParseError {

 case class Formatter(colors: Boolean)
   extends LessError.Formatter[ParseError] with Extracts {
   def apply(e: ParseError) = 
     "Parse error on line: %s, column: %s (%s)%s" format(
       e.line, e.column, e.message,
       showExtract(e.line, e.column, e.extract, colors))
  }

  def is(props: Map[String, Any]) =
    (props.get(LessError.Name).filter(_ == "ParseError").isDefined
     || props.get(LessError.Type).filter(_ == "Parse").isDefined)
}

case class ParseError(
  line: Int, column: Int, message: String, extract: Seq[String], formatter: LessError.Formatter[ParseError])
  extends CompilationError with LessError.Formatted[ParseError] {
  def withFormatter(f: LessError.Formatter[ParseError]) =
    copy(formatter = f)
  override def getMessage = formatter(this)
}

object UndefinedVar {
  case class Formatter(colors: Boolean)
    extends LessError.Formatter[UndefinedVar] with Extracts {
    def apply(e: UndefinedVar) =
      "Undefined variable %s on line: %s, column: %s%s" format(
        err(colors, e.name), e.line, e.column,
        showExtract(e.line, e.column, e.extract, colors))
  }
}

case class UndefinedVar(
  name: String, line: Int, column: Int, extract: Seq[String],
  formatter: LessError.Formatter[UndefinedVar])
  extends CompilationError with LessError.Formatted[UndefinedVar] {
  def withFormatter(f: LessError.Formatter[UndefinedVar]) =
    copy(formatter = f)
  override def getMessage = formatter(this)
}

case class GenericLessError(props: Map[String, Any])
  extends CompilationError {
  override def getMessage = "Less error:\n%s" format(props.map {
    case (k, v) => "%s: %s" format(
      k, v match {
        case seq: Seq[_] =>
          seq
        case any =>
          any
      }
    )
  }.mkString("\n"))
}
