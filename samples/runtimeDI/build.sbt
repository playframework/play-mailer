import java.io.File
import PlayKeys._

name := "runtime-DI"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer-guice" % "7.0.2-SNAPSHOT",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalacOptions ++= Seq("-deprecation", "-Xfatal-warnings")

// This sample project has both Scala and Java code.
javacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-parameters",
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
)
