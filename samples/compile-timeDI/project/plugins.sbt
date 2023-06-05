addSbtPlugin("com.typesafe.play" % "sbt-plugin" % sys.env.getOrElse("PLAY_VERSION", "2.9.0-M5"))
addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.0.1")
