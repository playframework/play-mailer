import java.io.File
import PlayKeys._

name := "compile-time-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

scalaVersion := "2.13.10"

crossScalaVersions := Seq("2.12.17", "2.13.10")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-deprecation", "-Xfatal-warnings")
