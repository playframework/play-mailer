import com.typesafe.sbt.SbtScalariform._
import com.typesafe.tools.mima.core._
import com.typesafe.tools.mima.plugin.MimaPlugin._
import interplay.ScalaVersions
import scalariform.formatter.preferences._

lazy val commonSettings = mimaDefaultSettings ++ Seq(
  // scalaVersion needs to be kept in sync with travis-ci
  scalaVersion := ScalaVersions.scala213,
  crossScalaVersions := Seq(ScalaVersions.scala212, ScalaVersions.scala213),
  scalariformAutoformat := true,
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(DoubleIndentConstructorArguments, true),

  scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",

    "-Ywarn-unused:imports",
    "-Xlint:nullary-unit",

    "-Xlint",
    "-Ywarn-dead-code"
  ),

  javacOptions ++= Seq(
    "-Xlint:unchecked",
    "-Xlint:deprecation"
  ),

  mimaBinaryIssueFilters ++= Seq(
  )
)

// needs to be kept in sync with travis-ci
val PlayVersion = playVersion(sys.env.getOrElse("PLAY_VERSION", "2.8.0-M3"))

// Version used to check binary compatibility
val mimaPreviousArtifactsVersion = "7.0.0"

def mimaPreviousVersionExcludeScala213(scalaVersion: String, modules: Set[ModuleID]): Set[ModuleID] = {
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, v)) if v >= 13 => Set.empty
    case _                       => modules
  }
}

lazy val `play-mailer` = (project in file("play-mailer"))
  .enablePlugins(PlayLibrary)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "com.typesafe" % "config" % "1.3.3",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "org.apache.commons" % "commons-email" % "1.5",
      "com.typesafe.play" %% "play" % PlayVersion % Test,
      "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
    ),
    mimaPreviousArtifacts := mimaPreviousVersionExcludeScala213(
      scalaVersion.value,
      Set("com.typesafe.play" %% "play-mailer" % mimaPreviousArtifactsVersion)
    )
  )

lazy val `play-mailer-guice` = (project in file("play-mailer-guice"))
  .enablePlugins(PlayLibrary)
  .settings(commonSettings)
  .dependsOn(`play-mailer`)
  .settings(
    libraryDependencies ++= Seq(
      "com.google.inject" % "guice" % "4.2.2",
      "com.typesafe.play" %% "play" % PlayVersion % Test,
      "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
    ),
    mimaPreviousArtifacts := mimaPreviousVersionExcludeScala213(
      scalaVersion.value,
      Set("com.typesafe.play" %% "play-mailer-guice" % mimaPreviousArtifactsVersion)
    )
  )

lazy val `play-mailer-root` = (project in file("."))
  .enablePlugins(PlayRootProject, PlayReleaseBase)
  .settings(commonSettings)
  .aggregate(`play-mailer`, `play-mailer-guice`)

playBuildRepoName in ThisBuild := "play-mailer"
