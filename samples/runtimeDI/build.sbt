import java.io.File
import PlayKeys._

name := "runtime-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

scalaVersion := "2.13.11"

crossScalaVersions := Seq("2.13.11", "3.3.0")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer-guice" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M6" % Test
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

resolvers ++= Resolver.sonatypeOssRepos("snapshots")
