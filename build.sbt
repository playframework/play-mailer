import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifacts
import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import interplay.ScalaVersions._

lazy val `play-mailer` = (project in file("."))
  .enablePlugins(PlayLibrary, PlayReleaseBase)
  .settings(
    scalaVersion := scala212,
    crossScalaVersions := Seq(scala211, scala212)
  )

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