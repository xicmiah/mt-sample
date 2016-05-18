name := "transfers-http-api"

version := "0.1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-target:jvm-1.8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "2.4.4",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.4",
  "de.heikoseeberger" %% "akka-http-upickle" % "1.5.3"
)