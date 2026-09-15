resolvers += Resolver.sonatypeCentralSnapshots

addSbtPlugin(
  "org.playframework" % "sbt-plugin" % sys.env.getOrElse("PLAY_VERSION", "3.1.0-M10-e1f3c2a9-SNAPSHOT")
)
addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.1.1")
