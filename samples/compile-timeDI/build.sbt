import java.io.File
import PlayKeys._

name := "compile-time-DI"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.1"

crossScalaVersions := Seq("2.12.10", "2.13.1")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "8.0.0-SNAPSHOT",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-deprecation", "-Xfatal-warnings")
