import sbt._

/**
  * Created by wardziniak on 28.09.2020.
  */
object Dependencies {

  lazy val KafkaStreamsScala: ModuleID = "org.apache.kafka" %% "kafka-streams-scala" % Versions.Kafka
  lazy val KafkaStreamsTestUtils: ModuleID = "org.apache.kafka" % "kafka-streams-test-utils" % Versions.Kafka % Test
  lazy val ScalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.ScalaTest % Test

  // Logging
  lazy val ScalaLogging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
  lazy val LogbackClassic: ModuleID = "ch.qos.logback" % "logback-classic" % Versions.logbackClassic
  lazy val Logging: Seq[ModuleID] = Seq(ScalaLogging, LogbackClassic)
  lazy val PureConfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % Versions.PureConfig

  // Json
  lazy val PlayJson: ModuleID = "com.typesafe.play" %% "play-json" % Versions.PlayJson
  lazy val PlayJsonDerivedCodecs: ModuleID = "org.julienrf" %% "play-json-derived-codecs" % Versions.PlayJsonDerivedCodecs
  lazy val Serdes: Seq[ModuleID] = Seq(PlayJson, PlayJsonDerivedCodecs)

  val AtmDependencies: Seq[ModuleID] = Seq(KafkaStreamsScala, KafkaStreamsTestUtils, ScalaTest, PureConfig) ++ Logging ++ Serdes


}
