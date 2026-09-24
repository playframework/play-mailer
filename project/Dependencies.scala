import sbt._

object Dependencies {
  val scala213Version   = "2.13.18"
  val scala33LTSVersion = "3.3.8"
  val scala39LTSVersion = "3.9.0"
  val scala3NextVersion = "3.10.0-RC2"

  val publishedScalaVersions = Seq(scala213Version, scala33LTSVersion)

  private val scalaVersionAliases = Map(
    "2.13.x" -> scala213Version,
    "3.3.x"  -> scala33LTSVersion,
    "3.9.x"  -> scala39LTSVersion,
    "3.next" -> scala3NextVersion,
  )

  def resolveScalaVersion(version: String): String = scalaVersionAliases.getOrElse(version, version)

  val PlayVersion = sys.props.getOrElse(
    "play.version",
    sys.env.getOrElse("PLAY_VERSION", "3.1.0-M10-e1f3c2a9-SNAPSHOT")
  )
}
