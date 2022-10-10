lazy val plugins = (project in file(".")).settings(
  scalaVersion := "2.12.17", // TODO: remove when upgraded to sbt 1.8.0
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % sys.env.getOrElse("PLAY_VERSION", "2.9.0-M2"))
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
