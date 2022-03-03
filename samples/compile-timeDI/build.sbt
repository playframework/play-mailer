import java.io.File
import PlayKeys._

name := "compile-time-DI"

ThisBuild / dynverVTagPrefix := false

scalaVersion := "2.13.8"

crossScalaVersions := Seq("2.12.13", "2.13.8")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-deprecation", "-Xfatal-warnings")
