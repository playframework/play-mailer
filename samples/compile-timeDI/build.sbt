import java.io.File
import PlayKeys._

name := "compile-time-DI"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "5.0.1-SNAPSHOT"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
