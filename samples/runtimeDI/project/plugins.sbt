addSbtPlugin("org.playframework" % "sbt-plugin" % sys.env.getOrElse("PLAY_VERSION", "3.0.0"))
addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.0.1")
