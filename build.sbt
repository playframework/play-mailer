import interplay.ScalaVersions

import com.typesafe.tools.mima.core._
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import com.typesafe.tools.mima.plugin.MimaKeys.{
  mimaBinaryIssueFilters, mimaPreviousArtifacts
}

// Common settings
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

lazy val commonSettings = SbtScalariform.scalariformSettings ++ Seq(
  scalaVersion := ScalaVersions.scala211,
  crossScalaVersions := Seq(ScalaVersions.scala211, ScalaVersions.scala212),
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(DoubleIndentClassDeclaration, true)
)

lazy val playMailerMimaSettings = mimaDefaultSettings ++ Seq(
  mimaPreviousArtifacts := Set(
    "com.typesafe.play" % "play-mailer_2.11" % "5.0.0"
  )
)

val PlayVersion = playVersion("2.6.0-RC2")

lazy val `play-mailer` = (project in file("play-mailer"))
  .enablePlugins(PlayLibrary)
  .settings(commonSettings)
  .settings(playMailerMimaSettings)
  .settings(
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "com.typesafe" % "config" % "1.3.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "org.apache.commons" % "commons-email" % "1.4",
      "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
    )
  )

lazy val `play-mailer-guice` = (project in file("play-mailer-guice"))
  .enablePlugins(PlayLibrary)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.google.inject" % "guice" % "4.0",
      "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
    )
  )
  .dependsOn(`play-mailer`)

lazy val root = (project in file("."))
  .enablePlugins(PlayRootProject)
  .aggregate(`play-mailer`, `play-mailer-guice`)
  .settings(commonSettings: _*)

playBuildRepoName in ThisBuild := "play-mailer"
