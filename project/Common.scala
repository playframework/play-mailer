import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Common extends AutoPlugin {
  override def trigger = allRequirements

  override def requires = JvmPlugin

  val repoName = "play-mailer"

  override def globalSettings =
    Seq(
      organization := "org.playframework",
      organizationName := "The Play Framework Project",
      organizationHomepage := Some(uri("https://playframework.com/")),
      homepage := Some(uri(s"https://github.com/playframework/${repoName}")),
      licenses := Seq(License("Apache-2.0", uri("https://www.apache.org/licenses/LICENSE-2.0.html"))),

      scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-encoding", "utf8"),
      javacOptions ++= Seq("-encoding", "UTF-8", "-Xlint:-options"),

      scmInfo := Some(ScmInfo(uri(s"https://github.com/playframework/${repoName}"), s"scm:git:git@github.com:playframework/${repoName}.git")),
      developers += Developer("playframework",
        "The Play Framework Contributors",
        "contact@playframework.com",
        uri("https://github.com/playframework")),

      description := "Play mailer plugin")
}
