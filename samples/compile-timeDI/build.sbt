import java.io.File
import PlayKeys._

name := "compile-time-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

scalaVersion := "3.9.0"

crossScalaVersions := Seq("3.9.0", "3.8.3")

libraryDependencies ++= Seq(
  "org.playframework" %% "play-mailer" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-Werror") // "-deprecation" gets set by Play already

resolvers += Resolver.sonatypeCentralSnapshots
