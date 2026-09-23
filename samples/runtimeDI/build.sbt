import java.io.File
import PlayKeys._

name := "runtime-DI"

ThisBuild / dynverVTagPrefix := false

ThisBuild / dynverSonatypeSnapshots := true

val scalaVersionAliases = Map(
  "2.13.x" -> "2.13.18",
  "3.3.x"  -> "3.3.8",
  "3.9.x"  -> "3.9.0",
  "3.next" -> "3.10.0-RC2",
)

scalaVersion := {
  val selected = sys.props.getOrElse("scala.version", "2.13.x")
  scalaVersionAliases.getOrElse(selected, selected)
}

crossScalaVersions := Seq("2.13.x", "3.3.x").map(scalaVersionAliases)

scalacOptions ++= {
  if (scalaVersion.value.startsWith("3.3.")) Seq("-release:17", "-Yfuture-lazy-vals") else Seq.empty
}

libraryDependencies ++= Seq(
  "org.playframework" %% "play-mailer-guice" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2+52-be104c90-SNAPSHOT" % Test
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

resolvers ++= Seq(
  Resolver.sonatypeCentralSnapshots,
  Resolver.ApacheMavenSnapshotsRepo
)
