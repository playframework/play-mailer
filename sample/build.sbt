import java.io.File
import PlayKeys._

name := "j"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

resolvers += Resolver.file("LocalIvy", file(Path.userHome + File.separator + ".ivy2" + File.separator + "local"))(Resolver.ivyStylePatterns)

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.4.0-M2-SNAPSHOT"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava)