import sbt._

object Dependencies {
  // scalaVersion needs to be kept in sync with ci
  val Scala212 = "2.12.17"
  val Scala213 = "2.13.10"
  val ScalaVersions = Seq(Scala212, Scala213)

  val PlayVersion = sys.props.getOrElse("play.version", sys.env.getOrElse("PLAY_VERSION", "2.8.16"))
}
