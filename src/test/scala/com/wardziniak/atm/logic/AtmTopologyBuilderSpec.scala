package com.wardziniak.atm.logic

import java.util.Properties

import com.wardziniak.atm.config.AtmConfiguration
import com.wardziniak.atm.dto.RequestStatus.{Rejected, Success}
import com.wardziniak.atm.dto.RequestType.{Deposit, Withdraw}
import com.wardziniak.atm.dto.{AccountNumber, _}
import com.wardziniak.atm.model.Account
import com.wardziniak.atm.serdes.Serdes._
import com.wardziniak.atm.utils.ConfigurationLoader
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.{StreamsConfig, TestInputTopic, TestOutputTopic, TopologyTestDriver}
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by wardziniak on 29.09.2020.
  */
class AtmTopologyBuilderSpec extends WordSpec with Matchers {

  lazy val config: AtmConfiguration = ConfigurationLoader.loadConfig

  "AtmTopology" should {
    "return reject if account doesn't exist" in {
      val builder: StreamsBuilder = new StreamsBuilder()
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "AtmTopologyBuilderSpec1")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

      val topology = new AtmTopologyBuilder{}.buildTopology(builder, config)

      val testDriver = new TopologyTestDriver(topology, props)

      val requests: TestInputTopic[AtmRequestKey, AtmRequest] = testDriver.createInputTopic[AtmRequestKey, AtmRequest](config.api.inputTopic, AtmRequestKeySerde.serializer(), AtmRequestSerde.serializer())
      val responses: TestOutputTopic[AtmRequestKey, AtmResponse] = testDriver.createOutputTopic[AtmRequestKey, AtmResponse](config.api.outputTopic, AtmRequestKeySerde.deserializer(), AtmResponseSerde.deserializer())

      val userWithAccount = UserWithAccount(userId = 111, accountNumber = "1212")
      val requestId = "1234-sdaf"

      requests.pipeInput(userWithAccount, AtmRequest(user = userWithAccount, requestId = requestId, amount = 1.23, requestType = Withdraw))
      val out1 = responses.readRecord()
      out1.value() shouldBe AtmResponse(requestId = requestId, Rejected("Unknown Account 1212"))
      testDriver.close()
    }

    "return reject if amount is negative" in {
      val builder: StreamsBuilder = new StreamsBuilder()
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "AtmTopologyBuilderSpec2")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

      val topology = new AtmTopologyBuilder{}.buildTopology(builder, config)

      val testDriver = new TopologyTestDriver(topology, props)

      val requests: TestInputTopic[AtmRequestKey, AtmRequest] = testDriver.createInputTopic[AtmRequestKey, AtmRequest](config.api.inputTopic, AtmRequestKeySerde.serializer(), AtmRequestSerde.serializer())
      val responses: TestOutputTopic[AtmRequestKey, AtmResponse] = testDriver.createOutputTopic[AtmRequestKey, AtmResponse](config.api.outputTopic, AtmRequestKeySerde.deserializer(), AtmResponseSerde.deserializer())
      val accounts: TestInputTopic[AccountNumber, Account] = testDriver.createInputTopic[AccountNumber, Account](config.accountInfo.accountTopic, AccountNumberSerde.serializer(), AccountSerde.serializer())


      val userWithAccount = UserWithAccount(userId = 111, accountNumber = "1212")
      val requestId = "1234-5678"

      accounts.pipeInput(userWithAccount.accountNumber, Account(accountNumber = userWithAccount.accountNumber, balance = 5.01))

      requests.pipeInput(userWithAccount, AtmRequest(user = userWithAccount, requestId = requestId, amount = -5.02, requestType = Withdraw))
      val out1 = responses.readRecord()
      out1.value() shouldBe AtmResponse(requestId = requestId, Rejected("Amount cannot be negative"))
      testDriver.close()

    }

    "decrease balance after Withdraw" in {
      val builder: StreamsBuilder = new StreamsBuilder()
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "AtmTopologyBuilderSpec3")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

      val topology = new AtmTopologyBuilder{}.buildTopology(builder, config)

      val testDriver = new TopologyTestDriver(topology, props)

      val requests: TestInputTopic[AtmRequestKey, AtmRequest] = testDriver.createInputTopic[AtmRequestKey, AtmRequest](config.api.inputTopic, AtmRequestKeySerde.serializer(), AtmRequestSerde.serializer())
      val responses: TestOutputTopic[AtmRequestKey, AtmResponse] = testDriver.createOutputTopic[AtmRequestKey, AtmResponse](config.api.outputTopic, AtmRequestKeySerde.deserializer(), AtmResponseSerde.deserializer())
      val accounts: TestInputTopic[AccountNumber, Account] = testDriver.createInputTopic[AccountNumber, Account](config.accountInfo.accountTopic, AccountNumberSerde.serializer(), AccountSerde.serializer())


      val userWithAccount = UserWithAccount(userId = 111, accountNumber = "1212")
      val requestId = "1234-5678"

      accounts.pipeInput(userWithAccount.accountNumber, Account(accountNumber = userWithAccount.accountNumber, balance = 10.00))

      requests.pipeInput(userWithAccount, AtmRequest(user = userWithAccount, requestId = requestId, amount = 5.02, requestType = Withdraw))

      val accountStateStore: KeyValueStore[AccountNumber, Account] = testDriver.getKeyValueStore(config.accountInfo.accountStateStore)
      val account = accountStateStore.get(userWithAccount.accountNumber)
      account.balance shouldBe 4.98
      val out1 = responses.readRecord()
      out1.value() shouldBe AtmResponse(requestId = requestId, Success)
      testDriver.close()
    }

    "increase balance after deposit" in {
      val builder: StreamsBuilder = new StreamsBuilder()
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "AtmTopologyBuilderSpec4")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

      val topology = new AtmTopologyBuilder{}.buildTopology(builder, config)

      val testDriver = new TopologyTestDriver(topology, props)

      val requests: TestInputTopic[AtmRequestKey, AtmRequest] = testDriver.createInputTopic[AtmRequestKey, AtmRequest](config.api.inputTopic, AtmRequestKeySerde.serializer(), AtmRequestSerde.serializer())
      val responses: TestOutputTopic[AtmRequestKey, AtmResponse] = testDriver.createOutputTopic[AtmRequestKey, AtmResponse](config.api.outputTopic, AtmRequestKeySerde.deserializer(), AtmResponseSerde.deserializer())
      val accounts: TestInputTopic[AccountNumber, Account] = testDriver.createInputTopic[AccountNumber, Account](config.accountInfo.accountTopic, AccountNumberSerde.serializer(), AccountSerde.serializer())


      val userWithAccount = UserWithAccount(userId = 111, accountNumber = "1212")
      val requestId = "1234-5678"

      accounts.pipeInput(userWithAccount.accountNumber, Account(accountNumber = userWithAccount.accountNumber, balance = 10.00))

      requests.pipeInput(userWithAccount, AtmRequest(user = userWithAccount, requestId = requestId, amount = 5.02, requestType = Deposit))

      val accountStateStore: KeyValueStore[AccountNumber, Account] = testDriver.getKeyValueStore(config.accountInfo.accountStateStore)
      val account = accountStateStore.get(userWithAccount.accountNumber)
      account.balance shouldBe 15.02
      val out1 = responses.readRecord()
      out1.value() shouldBe AtmResponse(requestId = requestId, Success)
      testDriver.close()
    }

    "keep order of operations" in {
      val builder: StreamsBuilder = new StreamsBuilder()
      val props = new Properties()
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "AtmTopologyBuilderSpec4")
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")

      val topology = new AtmTopologyBuilder{}.buildTopology(builder, config)

      val testDriver = new TopologyTestDriver(topology, props)

      val requests: TestInputTopic[AtmRequestKey, AtmRequest] = testDriver.createInputTopic[AtmRequestKey, AtmRequest](config.api.inputTopic, AtmRequestKeySerde.serializer(), AtmRequestSerde.serializer())
      val responses: TestOutputTopic[AtmRequestKey, AtmResponse] = testDriver.createOutputTopic[AtmRequestKey, AtmResponse](config.api.outputTopic, AtmRequestKeySerde.deserializer(), AtmResponseSerde.deserializer())
      val accounts: TestInputTopic[AccountNumber, Account] = testDriver.createInputTopic[AccountNumber, Account](config.accountInfo.accountTopic, AccountNumberSerde.serializer(), AccountSerde.serializer())


      val userWithAccount = UserWithAccount(userId = 111, accountNumber = "1212")
      val requestId1 = "1234-0001"
      val requestId2 = "1234-0002"
      val requestId3 = "1234-0003"

      accounts.pipeInput(userWithAccount.accountNumber, Account(accountNumber = userWithAccount.accountNumber, balance = 10.00))

      requests.pipeInput(userWithAccount, AtmRequest(user = userWithAccount, requestId = requestId1, amount = 15.00, requestType = Withdraw))
      requests.pipeInput(userWithAccount, AtmRequest(user = userWithAccount, requestId = requestId2, amount = 5.00, requestType = Deposit))
      requests.pipeInput(userWithAccount, AtmRequest(user = userWithAccount, requestId = requestId3, amount = 15.00, requestType = Withdraw))

      val accountStateStore: KeyValueStore[AccountNumber, Account] = testDriver.getKeyValueStore(config.accountInfo.accountStateStore)
      val account = accountStateStore.get(userWithAccount.accountNumber)
      account.balance shouldBe 0.0
      responses.readRecord().value() shouldBe AtmResponse(requestId = requestId1, Rejected("Balance cannot drop below zero, current balance: 10.0"))
      responses.readRecord().value() shouldBe AtmResponse(requestId = requestId2, Success)
      responses.readRecord().value() shouldBe AtmResponse(requestId = requestId3, Success)
      testDriver.close()
    }
  }
}
