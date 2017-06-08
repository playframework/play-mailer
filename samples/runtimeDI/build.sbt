import java.io.File
import PlayKeys._

name := "runtime-DI"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "5.0.1-SNAPSHOT",
  "com.typesafe.play" %% "play-mailer-guice" % "5.0.1-SNAPSHOT",
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)
