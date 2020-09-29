package com.wardziniak.atm.utils

import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.atm.config.AtmConfiguration
import com.wardziniak.atm.dto.RequestType.{Deposit, Withdraw}
import com.wardziniak.atm.dto.{AtmRequest, UserWithAccount}
import com.wardziniak.atm.serdes.Serdes._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.util.Random

/**
  * Created by wardziniak on 29.09.2020.
  */
object TransactionProducerSupplierApp extends App with LazyLogging {
  val config: AtmConfiguration = ConfigurationLoader.loadConfig
  val rand = Random
  val props = ProducerConfigBuilder.build(
    config.bootstrapServer,
    "com.wardziniak.atm.partitioners.AccountsPartitioner"
  )
  val producer = new KafkaProducer[UserWithAccount, AtmRequest](props, AtmRequestKeySerde.serializer(), AtmRequestSerde.serializer())

  Stream.from(1).map(createRecord).take(10).map(producer.send).map(_.get()).toList

  private def createRecord(i: Int): ProducerRecord[UserWithAccount, AtmRequest] = {
    Thread.sleep(Math.abs(rand.nextLong()%100 * 10))
    val key = UserWithAccount(1, "1")
    val requestType = if (i % 2 == 0) Deposit else Withdraw
    val value = AtmRequest(user = key, requestId = s"requestId_$i", amount = 400, requestType = requestType)
    val record = new ProducerRecord[UserWithAccount, AtmRequest](config.api.inputTopic, key, value)
    record
  }


}
