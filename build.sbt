name := "play-mailer"
    
organization := "com.typesafe.play"

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.11.1", "2.10.4")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.6" % "provided",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "org.specs2" %% "specs2-core" % "2.4.9" % "test"
)

javacOptions ++= Seq("-source", "1.6", "-target", "1.6", "-Xlint:unchecked", "-encoding", "UTF-8")

scalacOptions += "-deprecation"

// Publish settings
publishTo := {
  if (isSnapshot.value) Some(Opts.resolver.sonatypeSnapshots)
  else Some(Opts.resolver.sonatypeStaging)
}

homepage := Some(url("https://github.com/playframework/play-mailer"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

pomExtra := {
  <scm>
    <url>https://github.com/playframework/play-mailer</url>
    <connection>scm:git:git@github.com:playframework/play-mailer.git</connection>
  </scm>
  <developers>
    <developer>
      <id>playframework</id>
      <name>Play Framework Team</name>
      <url>https://github.com/playframework</url>
    </developer>
  </developers>
}

pomIncludeRepository := { _ => false }

// Release settings
releaseSettings

ReleaseKeys.publishArtifactsAction := PgpKeys.publishSigned.value
    
ReleaseKeys.crossBuild := true

ReleaseKeys.tagName := (version in ThisBuild).value
