ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val circeVersion = "0.14.1"

lazy val circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

lazy val commonDeps = Seq(
  "org.typelevel" %% "cats-core" % "2.7.0",
  "org.typelevel" %% "cats-effect" % "3.3.14",
  "dev.profunktor" %% "redis4cats-streams" % "1.2.0",
  "dev.profunktor" %% "redis4cats-effects" % "1.2.0"
) ++ circe

lazy val root = (project in file("."))
  .settings(
    name := "order-system",
    scalacOptions += "-Ypartial-unification"
  )
  .aggregate(
    `async-creator`,
    `courier-service`,
    `assignment-service`
  )

lazy val `async-creator` = (project in file("async-creator"))
  .settings(
    libraryDependencies ++= commonDeps ++ Seq(
      "co.fs2" %% "fs2-core" % "3.2.12",
      "io.laserdisc" %% "fs2-aws-sqs" % "5.0.2"
    ))

lazy val `courier-service` = (project in file("courier-service"))
lazy val `assignment-service` = (project in file("assignment-service"))
