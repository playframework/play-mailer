lazy val `play-mailer` = (project in file("."))
  .enablePlugins(PlayLibrary, PlayReleaseBase)
    
val PlayVersion = playVersion("2.4.0")

scalaVersion := "2.11.6"
crossScalaVersions := Seq("2.10.5", "2.11.6")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % PlayVersion % Provided,
  "org.apache.commons" % "commons-email" % "1.3.3",
  "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

playBuildRepoName in ThisBuild := "play-mailer"

