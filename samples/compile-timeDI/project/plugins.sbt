addSbtPlugin("com.typesafe.play" % "sbt-plugin" % sys.env.getOrElse("PLAY_VERSION", "2.8.0"))
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.0.0")
