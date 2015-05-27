lazy val `play-mailer` = (project in file("."))
  .enablePlugins(PlayLibrary, PlayReleaseBase)
    
val PlayVersion = playVersion("2.4.0")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % PlayVersion % Provided,
  "org.apache.commons" % "commons-email" % "1.3.3",
  "com.typesafe.play" %% "play-specs2" % PlayVersion % Test
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

playBuildRepoName in ThisBuild := "play-mailer"

