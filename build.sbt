import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

ThisBuild / dynverVTagPrefix := false

lazy val commonSettings = Seq(
  scalaVersion := Dependencies.Scala213,
  crossScalaVersions := Dependencies.ScalaVersions,
  scalariformAutoformat := true,
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(DoubleIndentConstructorArguments, true),

  scalacOptions ++= Seq(
    "-release",
    "11",
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
)

val previousVersion: Option[String] = None

lazy val `play-mailer` = (project in file("play-mailer"))
  .enablePlugins(Common)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "com.typesafe" % "config" % "1.4.3",
      "org.slf4j" % "slf4j-api" % "2.0.16",
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
      "com.google.inject" % "guice" % "6.0.0",
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

