package com.wardziniak.atm.app

import java.time.Duration

import com.wardziniak.atm.PropertyBuilder
import com.wardziniak.atm.config.AtmConfiguration
import com.wardziniak.atm.logic.TopologyBuilder
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder

/**
  * Created by wardziniak on 28.09.2020.
  */
trait KafkaRunner { self: TopologyBuilder =>


  def startApp(config: AtmConfiguration): Unit = {
    val properties = PropertyBuilder(config.kafkaProperties)
      .withAppId(config.applicationId)
      .withBootstrapServer(config.bootstrapServer)

    val builder: StreamsBuilder = new StreamsBuilder()
    val streams: KafkaStreams = new KafkaStreams(self.buildTopology(streamsBuilder = builder, atmConfig = config), properties.build)
    streams.start()
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      streams.close(Duration.ofSeconds(10))
    }))



  }


}
