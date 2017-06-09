import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifacts
import interplay.ScalaVersions._

lazy val `play-mailer` = (project in file("."))
  .enablePlugins(PlayLibrary, PlayReleaseBase)
  .settings(
    scalaVersion := scala212,
    crossScalaVersions := Seq(scala211, scala212)
  )
    
val PlayVersion = playVersion("2.6.0-RC2")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % PlayVersion % Provided,
  "org.apache.commons" % "commons-email" % "1.4",
  "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

playBuildRepoName in ThisBuild := "play-mailer"

mimaDefaultSettings

previousArtifacts := Set  (
  "com.typesafe.play" % "play-mailer_2.11" % "5.0.0"
)