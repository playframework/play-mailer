import java.io.File
import PlayKeys._

name := "compile-time-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

scalaVersion := "2.13.17"

crossScalaVersions := Seq("2.13.17", "3.3.6")

libraryDependencies ++= Seq(
  "org.playframework" %% "play-mailer" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-Werror") // "-deprecation" gets set by Play already

resolvers += Resolver.sonatypeCentralSnapshots
