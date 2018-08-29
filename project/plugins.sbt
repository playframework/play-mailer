addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.getOrElse("interplay.version", "1.3.17"))
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.3.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
