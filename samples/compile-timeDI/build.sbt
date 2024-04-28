import java.io.File
import PlayKeys._

name := "compile-time-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

scalaVersion := "2.13.13"

crossScalaVersions := Seq("2.13.13", "3.3.3")

libraryDependencies ++= Seq(
  "org.playframework" %% "play-mailer" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-Werror") // "-deprecation" gets set by Play already

resolvers ++= Resolver.sonatypeOssRepos("snapshots")
