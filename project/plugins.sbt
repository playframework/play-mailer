addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.getOrElse("interplay.version", "1.3.5"))

addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.13")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")