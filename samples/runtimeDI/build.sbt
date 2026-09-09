import java.io.File
import PlayKeys._

name := "runtime-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

scalaVersion := "3.9.0"

crossScalaVersions := Seq("3.9.0", "3.8.3")

libraryDependencies ++= Seq(
  "org.playframework" %% "play-mailer-guice" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalacOptions ++= Seq("-Werror") // "-deprecation" gets set by Play already

// This sample project has both Scala and Java code.
javacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-parameters",
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
)

resolvers += Resolver.sonatypeCentralSnapshots
