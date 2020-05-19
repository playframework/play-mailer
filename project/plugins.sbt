addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.getOrElse("interplay.version", "2.1.5"))
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.3.0")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.0")