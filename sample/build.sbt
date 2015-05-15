import java.io.File
import PlayKeys._

name := "j"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "3.0.0-M1-SNAPSHOT"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayJava)
