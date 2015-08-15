import com.typesafe.tools.mima.plugin.MimaPlugin.mimaDefaultSettings
import com.typesafe.tools.mima.plugin.MimaKeys.previousArtifact

lazy val `play-mailer` = (project in file("."))
  .enablePlugins(PlayLibrary, PlayReleaseBase)
    
val PlayVersion = playVersion("2.4.2")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % PlayVersion % Provided,
  "org.apache.commons" % "commons-email" % "1.3.3",
  "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

playBuildRepoName in ThisBuild := "play-mailer"

mimaDefaultSettings

previousArtifact := Some("com.typesafe.play" % "play-mailer_2.11" % "3.0.1")