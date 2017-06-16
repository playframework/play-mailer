import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import com.typesafe.sbt.SbtScalariform._
import interplay.ScalaVersions

import scalariform.formatter.preferences._

lazy val commonSettings = SbtScalariform.scalariformSettings ++ Seq(
  scalaVersion := ScalaVersions.scala212,
  crossScalaVersions := Seq(ScalaVersions.scala211, ScalaVersions.scala212),
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(DoubleIndentClassDeclaration, true)
)

lazy val `play-mailer` = (project in file("."))
  .enablePlugins(PlayLibrary, PlayReleaseBase)
  .settings(commonSettings)

// needs to be changed in .travis-ci.yml
val PlayVersion = playVersion(sys.env.getOrElse("PLAY_VERSION", "2.6.0-RC2"))

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.0", // 4.0 to maybe make it work with 2.5 and 2.6
  "com.typesafe" % "config" % "1.3.1",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.apache.commons" % "commons-email" % "1.4",
  "com.typesafe.play" %% "play" % PlayVersion % Test,
  "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
)

playBuildRepoName in ThisBuild := "play-mailer"

mimaDefaultSettings

previousArtifacts := Set(
  "com.typesafe.play" % "play-mailer_2.11" % "5.0.0"
)