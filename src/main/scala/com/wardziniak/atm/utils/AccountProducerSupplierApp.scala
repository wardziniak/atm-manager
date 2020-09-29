package com.wardziniak.atm.utils

import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.atm.config.AtmConfiguration
import com.wardziniak.atm.dto.AccountNumber
import com.wardziniak.atm.model.Account
import com.wardziniak.atm.serdes.Serdes._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

/**
  * Created by wardziniak on 29.09.2020.
  */
object AccountProducerSupplierApp extends App with LazyLogging {
  val config: AtmConfiguration = ConfigurationLoader.loadConfig
  val rand = Random
  val props = ProducerConfigBuilder.build(
    config.bootstrapServer,
    "com.wardziniak.atm.partitioners.AccountsPartitioner"
  )

  val producer = new KafkaProducer[AccountNumber, Account](props, AccountNumberSerde.serializer(), AccountSerde.serializer())

  Stream.from(1).map(createRecord).take(10).map(producer.send).map(_.get()).toList

  private def createRecord(i: Int): ProducerRecord[AccountNumber, Account] = {
    Thread.sleep(Math.abs(rand.nextLong()%100 * 10))
    val record = new ProducerRecord[AccountNumber, Account](config.accountInfo.accountTopic, s"$i", Account(s"$i", 1000))
    record
  }
}
