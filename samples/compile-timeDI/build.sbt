import java.io.File
import PlayKeys._

name := "compile-time-DI"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "7.0.0-SNAPSHOT",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-deprecation", "-Xfatal-warnings")