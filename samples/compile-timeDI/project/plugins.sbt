addSbtPlugin("com.typesafe.play" % "sbt-plugin" % sys.env.getOrElse("PLAY_VERSION", "2.8.16"))
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
