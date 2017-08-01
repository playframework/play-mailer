import com.typesafe.sbt.SbtScalariform._
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import interplay.ScalaVersions

import scalariform.formatter.preferences._

lazy val commonSettings = SbtScalariform.scalariformSettings ++ Seq(
  // scalaVersion needs to be kept in sync with travis-ci
  scalaVersion := ScalaVersions.scala212,
  crossScalaVersions := Seq(ScalaVersions.scala211, ScalaVersions.scala212),
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(DoubleIndentClassDeclaration, true)
)

// needs to be kept in sync with travis-ci
val PlayVersion = playVersion(sys.env.getOrElse("PLAY_VERSION", "2.6.2"))

lazy val `play-mailer` = (project in file("play-mailer"))
  .enablePlugins(PlayLibrary)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "com.typesafe" % "config" % "1.3.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "org.apache.commons" % "commons-email" % "1.5",
      "com.typesafe.play" %% "play" % PlayVersion % Test,
      "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
    )
  )

lazy val `play-mailer-guice` = (project in file("play-mailer-guice"))
  .enablePlugins(PlayLibrary)
  .settings(commonSettings)
  .dependsOn(`play-mailer`)
  .settings(
    libraryDependencies ++= Seq(
      "com.google.inject" % "guice" % "4.0", // 4.0 to maybe make it work with 2.5 and 2.6
      "com.typesafe.play" %% "play" % PlayVersion % Test,
      "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
    )
  )

lazy val `play-mailer-root` = (project in file("."))
  .enablePlugins(PlayRootProject, PlayReleaseBase)
  .settings(commonSettings)
  .aggregate(`play-mailer`, `play-mailer-guice`)

playBuildRepoName in ThisBuild := "play-mailer"

mimaDefaultSettings

previousArtifacts := Set(
  "com.typesafe.play" % "play-mailer_2.11" % "5.0.0"
)