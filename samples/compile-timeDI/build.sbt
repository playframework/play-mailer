import java.io.File
import PlayKeys._

name := "compile-time-DI"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "3.0.2-SNAPSHOT"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)
