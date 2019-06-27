addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.getOrElse("interplay.version", "2.0.8"))
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.3.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")
