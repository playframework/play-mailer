import com.typesafe.tools.mima.core.IncompatibleMethTypeProblem
import com.typesafe.tools.mima.core.MissingTypesProblem
import com.typesafe.tools.mima.core.ProblemFilters

ThisBuild / dynverVTagPrefix := false

ThisBuild / resolvers ++= Seq(
  Resolver.sonatypeCentralSnapshots,
  Resolver.ApacheMavenSnapshotsRepo
)

lazy val commonSettings = Seq(
  scalaVersion := Dependencies.resolveScalaVersion(sys.props.getOrElse("scala.version", Dependencies.scala213Version)),
  crossScalaVersions := Dependencies.publishedScalaVersions,

  scalacOptions ++= Seq(
    "-release",
    "17",
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked"
  ) ++ {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) => Seq("-Ywarn-unused:imports", "-Xlint:nullary-unit", "-Xlint", "-Ywarn-dead-code")
      case Some((3, _)) => Seq("-Wunused:imports", "-Wshadow:all")
      case _            => Seq.empty
    }
  } ++ (if (scalaVersion.value.startsWith("3.3.")) Seq("-Yfuture-lazy-vals") else Seq.empty),

  javacOptions ++= Seq(
    "-Xlint:unchecked",
    "-Xlint:deprecation"
  ),

  exportJars := false,

  mimaBinaryIssueFilters ++= Seq(
    ProblemFilters.exclude[IncompatibleMethTypeProblem]("play.api.libs.mailer.SMTPDynamicMailer.this"),
    ProblemFilters.exclude[MissingTypesProblem]("play.api.libs.mailer.SMTPConfigurationProvider"),
  ),
)

val previousVersion: Option[String] = Some("11.0.0-M1")

lazy val `play-mailer` = (project in file("play-mailer"))
  .enablePlugins(Common)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "jakarta.inject" % "jakarta.inject-api" % "2.0.1",
      "com.typesafe" % "config" % "1.4.9",
      "org.slf4j" % "slf4j-api" % "2.0.19",
      "org.apache.commons" % "commons-email2-jakarta" % "2.0.0-M1",
      "org.playframework" %% "play" % Dependencies.PlayVersion % Test,
      "org.playframework" %% "play-specs2" % Dependencies.PlayVersion % Test
    ),
    mimaPreviousArtifacts := previousVersion.map(organization.value %% moduleName.value % _).toSet,
  )

lazy val `play-mailer-guice` = (project in file("play-mailer-guice"))
  .enablePlugins(Common)
  .settings(commonSettings)
  .dependsOn(`play-mailer`)
  .settings(
    libraryDependencies ++= Seq(
      "com.google.inject" % "guice" % "7.0.0",
      "org.playframework" %% "play" % Dependencies.PlayVersion % Test,
      "org.playframework" %% "play-specs2" % Dependencies.PlayVersion % Test
    ),
    mimaPreviousArtifacts := previousVersion.map(organization.value %% moduleName.value % _).toSet,
  )

lazy val `play-mailer-root` = (project in file("."))
  .disablePlugins(MimaPlugin)
  .settings(commonSettings)
  .settings(
    crossScalaVersions := Nil,
    publish / skip := true
  )
  .aggregate(`play-mailer`, `play-mailer-guice`)
