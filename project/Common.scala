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
      organizationHomepage := Some(url("https://playframework.com/")),
      homepage := Some(url(s"https://github.com/playframework/${repoName}")),
      licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),

      scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-encoding", "utf8"),
      javacOptions ++= Seq("-encoding", "UTF-8", "-Xlint:-options"),

      scmInfo := Some(ScmInfo(url(s"https://github.com/playframework/${repoName}"), s"scm:git:git@github.com:playframework/${repoName}.git")),
      developers += Developer("playframework",
        "The Play Framework Contributors",
        "contact@playframework.com",
        url("https://github.com/playframework")),

      description := "Play mailer plugin")
}
