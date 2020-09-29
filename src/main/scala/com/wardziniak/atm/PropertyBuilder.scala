package com.wardziniak.atm

import java.util.Properties

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.streams.StreamsConfig

/**
  * Created by wardziniak on 28.09.2020.
  */

case class PropertyBuilder(properties: Map[String, String]) {
  def build: Properties = {
    val props = new Properties()
    properties.foreach(prop => props.put(prop._1, prop._2))
    props
  }

  def withAppId(appId: String) = this.copy(properties = properties.updated(StreamsConfig.APPLICATION_ID_CONFIG, appId))

  def withBootstrapServer(bootstrapServer: String) = this.copy(properties = properties.updated(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer))

  def withPartitioner(partitionerName: String) = this.copy(properties = properties.updated(ProducerConfig.PARTITIONER_CLASS_CONFIG, partitionerName))
}
