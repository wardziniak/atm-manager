package com.wardziniak.atm.utils

import java.util.Properties

import org.apache.kafka.clients.producer.ProducerConfig

/**
  * Created by wardziniak on 29.09.2020.
  */
object ProducerConfigBuilder {
  def build(bootstrapServer: String, partitioner: String) = {
    val properties = new Properties
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    properties.setProperty(ProducerConfig.PARTITIONER_CLASS_CONFIG, partitioner)
    properties
  }
}
