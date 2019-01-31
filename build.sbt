import com.typesafe.sbt.SbtScalariform._
import com.typesafe.tools.mima.core._
import com.typesafe.tools.mima.plugin.MimaPlugin._
import interplay.ScalaVersions

import scalariform.formatter.preferences._

lazy val commonSettings = mimaDefaultSettings ++ Seq(
  // scalaVersion needs to be kept in sync with travis-ci
  scalaVersion := ScalaVersions.scala212,
  crossScalaVersions := Seq(ScalaVersions.scala211, ScalaVersions.scala212, "2.13.0-M5"),
  scalariformAutoformat := true,
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(DoubleIndentConstructorArguments, true),

  javacOptions ++= Seq(
    "-Xlint:unchecked",
    "-Xlint:deprecation"
  ),

  mimaBinaryIssueFilters ++= Seq(
    ProblemFilters.exclude[DirectMissingMethodProblem]("play.api.libs.mailer.SMTPConfiguration.apply"),
    ProblemFilters.exclude[IncompatibleResultTypeProblem]("play.api.libs.mailer.SMTPConfiguration.apply$default$11"),
    ProblemFilters.exclude[IncompatibleResultTypeProblem]("play.api.libs.mailer.SMTPConfiguration.<init>$default$11"),
    ProblemFilters.exclude[DirectMissingMethodProblem]("play.api.libs.mailer.SMTPConfiguration.copy"),
    ProblemFilters.exclude[IncompatibleResultTypeProblem]("play.api.libs.mailer.SMTPConfiguration.copy$default$11"),
    ProblemFilters.exclude[DirectMissingMethodProblem]("play.api.libs.mailer.SMTPConfiguration.this")
  )
)

// needs to be kept in sync with travis-ci
val PlayVersion = playVersion(sys.env.getOrElse("PLAY_VERSION", "2.7.0"))

// Version used to check binary compatibility
val mimaPreviousArtifactsVersion = "6.0.0"

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
    mimaPreviousArtifacts := Set(
      "com.typesafe.play" %% "play-mailer" % mimaPreviousArtifactsVersion
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
    mimaPreviousArtifacts := Set(
      "com.typesafe.play" %% "play-mailer-guice" % mimaPreviousArtifactsVersion
    )
  )

lazy val `play-mailer-root` = (project in file("."))
  .enablePlugins(PlayRootProject, PlayReleaseBase)
  .settings(commonSettings)
  .aggregate(`play-mailer`, `play-mailer-guice`)

playBuildRepoName in ThisBuild := "play-mailer"
