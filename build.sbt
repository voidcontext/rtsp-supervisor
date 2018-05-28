name := "supervisor"

version := "0.2.0"

scalaVersion := "2.12.6"

scalacOptions ++= Seq(
  "-language:higherKinds",
  "-deprecation",
  "-unchecked",
  "-feature",
)

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.9.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "1.0.0-RC"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.12",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.12" % Test
)