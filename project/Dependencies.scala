import sbt._

/**
  * Created by wardziniak on 28.09.2020.
  */
object Dependencies {

  val KafkaStreamsScala: ModuleID = "org.apache.kafka" %% "kafka-streams-scala" % Versions.Kafka
  val KafkaStreamsTestUtils: ModuleID = "org.apache.kafka" % "kafka-streams-test-utils" % Versions.Kafka % Test


  val ScalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.ScalaTest % Test

  val AtmDependencies: Seq[ModuleID] = Seq(KafkaStreamsScala, KafkaStreamsTestUtils, ScalaTest)


}
