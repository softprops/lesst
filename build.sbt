organization := "me.lessis"

name := "lesst"

version := "0.1.2"

crossScalaVersions := Seq("2.9.3", "2.10.3")

description := "a chauffeur for scala patrons in the less css compilation party"

scalacOptions ++= Seq(Opts.compile.deprecation)//, "-feature")

libraryDependencies += "org.mozilla" % "rhino" % "1.7R4"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

licenses := Seq(
  ("MIT", url("https://github.com/softprops/%s/blob/%s/LICENSE"
              .format(name.value, version.value))))

publishArtifact in Test := false

seq(bintraySettings:_*)

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("less", "css", "lesscss", "lesst")

seq(lsSettings:_*)

(LsKeys.tags in LsKeys.lsync) := Seq("less", "css", "lesscss", "lesst")

(externalResolvers in LsKeys.lsync) := (resolvers in bintray.Keys.bintray).value
