addSbtPlugin("com.typesafe.play" % "sbt-plugin" % sys.env.getOrElse("PLAY_VERSION", "2.9.11"))
addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.1.1")
