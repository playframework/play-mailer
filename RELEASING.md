## Releasing

This library is released from the `master` branch via Bintray (for signing) to Maven Central/Sonatype.

**The ambition is to enable releasing by tag from Travis...**

Note: The 8.0.1 release was created manually.


### Manual release steps:

Releasing requires Bintray credentials for https://bintray.com/playframework/

1. create a git tag for the next version eg "8.0.2"

1. push the tag to Github

1. sbt +publish (requires Bintray credentials)

1. Go to [Bintray](https://bintray.com/playframework/maven/play-mailer/)
    1. select the release version
    1. go to "Maven Central" tab
    1. put in your Maven Central token and password to Sync it
1. Check availability at [Maven Central](https://repo1.maven.org/maven2/com/typesafe/play/play-mailer_2.13/) (it takes a while to show up)
