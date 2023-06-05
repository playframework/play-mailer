import java.io.File
import PlayKeys._

name := "compile-time-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

scalaVersion := "2.13.11"

crossScalaVersions := Seq("2.13.11", "3.3.0")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M6" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-Werror") // "-deprecation" gets set by Play already

resolvers ++= Resolver.sonatypeOssRepos("snapshots")
