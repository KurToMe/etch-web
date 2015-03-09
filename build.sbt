name := "etch"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "org.mongodb" %% "casbah" % "2.7.1",
  "org.scalatest" %% "scalatest" % "2.1.7",
  "commons-io" % "commons-io" % "2.4",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.9.17",
  "com.newrelic.agent.java" % "newrelic-agent" % "3.9.0"
)

