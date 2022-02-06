# Releasing

This is released from the `main` branch. Unless and older version needs patching, when it is release from the `7.0.x` branch. If there is no branch for the release that needs patching, create it from the tag.

## Cutting the release

### Requires contributor access

- Check the [draft release notes](https://github.com/playframework/play-mailer/releases) to see if everything is there
- Wait until [main build finished](https://travis-ci.com/github/playframework/play-mailer/builds) after merging the last PR
- Update the [draft release](https://github.com/playframework/play-mailer/releases) with the next tag version `$VERSION$` (eg. `8.0.3`), title and release description
- Check that Travis CI release build has executed successfully (Travis will start a [CI build](https://travis-ci.com/github/playframework/play-mailer/builds) for the new tag and publish artifacts to Bintray)

### Requires Bintray access

- Go to [Bintray](https://bintray.com/playframework/maven/play-mailer) and select the just released version
- Go to the Maven Central tab and sync with Sonatype (using your Sonatype TOKEN key and password) (you may watch progress in the [Staging repository](https://oss.sonatype.org/#stagingRepositories))
