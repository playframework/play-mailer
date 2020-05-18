addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.getOrElse("interplay.version", "2.1.4"))
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.6.1")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.3")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.0.0")
