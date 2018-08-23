import java.io.File
import PlayKeys._

name := "runtime-DI"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "7.0.0-SNAPSHOT"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)
