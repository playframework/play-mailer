import java.io.File
import PlayKeys._

name := "compile-time-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

scalaVersion := "2.13.16"

crossScalaVersions := Seq("2.13.16", "3.3.4")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.2" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-Werror") // "-deprecation" gets set by Play already

resolvers += Resolver.sonatypeCentralSnapshots
