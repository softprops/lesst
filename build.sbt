organization := "me.lessis"

name := "lesst"

version := "0.1.0-SNAPSHOT"

// todo: drop 2.9.2 either for try in next release
crossScalaVersions := Seq("2.9.2", "2.9.3", "2.10.0", "2.10.1")

description := "a chauffeur for scala patrons in the less css compilation party"

scalacOptions <++= (scalaVersion).map { sv =>
  if (sv.startsWith("2.10")) Seq(Opts.compile.deprecation, "-feature")
  else Seq(Opts.compile.deprecation)
}

libraryDependencies += "org.mozilla" % "rhino" % "1.7R3"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

licenses <<= (version)(v => Seq(
  ("MIT", url("https://github.com/softprops/lesst/blob/%s/LICENSE" format v))
))

publishTo := Some(Opts.resolver.sonatypeStaging)

publishArtifact in Test := false

publishMavenStyle := true

pomExtra := (
  <scm>
    <url>git@github.com:softprops/lesst.git</url>
    <connection>scm:git:git@github.com:softprops/lesst.git</connection>
  </scm>
  <developers>
    <developer>
      <id>softprops</id>
      <name>Doug Tangren</name>
      <url>https://github.com/softprops</url>
    </developer>
  </developers>
)

//seq(lsSettings:_*)

//(LsKeys.tags in LsKeys.lsync) := Seq("less", "css", "lesscss", "lesst")
