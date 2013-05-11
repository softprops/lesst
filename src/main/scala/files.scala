package lesst

import java.io.File
import scala.io.Source

object Files {
  val ImportsDelimiter = "\n"

  // thanks sbt.IO :)

  def newerThan(a: File, b: File): Boolean = a.exists && (!b.exists || a.lastModified > b.lastModified)
  def relativize(base: File, file: File): Option[String] = {
    val pathString = file.getAbsolutePath
    baseFileString(base) flatMap {
      baseString => {
        if (pathString.startsWith(baseString)) Some(pathString.substring(baseString.length))
        else None
      }
    }
  }
  private def baseFileString(baseFile: File): Option[String] = {
    if (baseFile.isDirectory) {
      val cp = baseFile.getAbsolutePath
      assert(cp.length > 0)
      val normalized = if(cp.charAt(cp.length - 1) == File.separatorChar) cp else cp + File.separatorChar
      Some(normalized)
    }
    else None
  }
}

class LessSourceFile(
  val lessFile: File,
  sourcesDir: File,
  targetDir: File,
  cssDir: File) {

  val relPath = Files.relativize(sourcesDir, lessFile).get
  lazy val cssFile = new File(cssDir, relPath.replaceFirst("\\.less$",".css"))
  lazy val importsFile = new File(targetDir, relPath + ".imports")
  lazy val parentDir = lessFile.getParentFile

  def imports = Source.fromFile(importsFile).getLines().collect {
    case fileName if fileName.trim.length > 0 => new File(parentDir, fileName)
  }

  def changed =
    (!importsFile.exists
    || (Files.newerThan(lessFile, cssFile))
    || (imports exists (Files.newerThan(_, cssFile))))

  def path = lessFile.getPath.replace('\\', '/')

  override def toString = lessFile.toString
}
