import java.io.File
import PlayKeys._

name := "compile-time-DI"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "6.0.0-SNAPSHOT"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
