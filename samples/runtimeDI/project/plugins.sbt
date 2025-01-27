addSbtPlugin("org.playframework" % "sbt-plugin" % sys.env.getOrElse("PLAY_VERSION", "3.1.0-M1"))
addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.1.0")
