package com.wardziniak.atm.logic

import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.atm.config.AtmConfiguration
import com.wardziniak.atm.dto.{AccountNumber, AtmRequest, AtmRequestKey}
import com.wardziniak.atm.model.Account
import com.wardziniak.atm.partitioners.AccountStreamPartitioner
import com.wardziniak.atm.serdes.Serdes
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.ProcessorSupplier
import org.apache.kafka.streams.scala.ImplicitConversions.consumedFromSerde
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.state.{KeyValueStore, StoreBuilder, Stores}

/**
  * Created by wardziniak on 28.09.2020.
  */
trait TopologyBuilder {
  def buildTopology(streamsBuilder: StreamsBuilder, atmConfig: AtmConfiguration): Topology
}

trait AtmTopologyBuilder extends TopologyBuilder with LazyLogging {
  override def buildTopology(streamsBuilder: StreamsBuilder, atmConfig: AtmConfiguration): Topology = {
    val accountStateStore = atmConfig.accountInfo.accountStateStore
    // It is safe to use different key for state store becasue assuption is that messages are partitioned according to AccountNumber
    val accountStoreSupplier: StoreBuilder[KeyValueStore[AccountNumber, Account]] = Stores.keyValueStoreBuilder[AccountNumber, Account](
      Stores.persistentKeyValueStore(accountStateStore),
      Serdes.AccountNumberSerde,
      Serdes.AccountSerde
    )
    streamsBuilder.addStateStore(accountStoreSupplier)

    import com.wardziniak.atm.serdes.Serdes.{AccountNumberSerde, AccountSerde, AtmRequestKeySerde, AtmRequestSerde, AtmResponseSerde}
    streamsBuilder.stream[AccountNumber, Account](atmConfig.accountInfo.accountTopic)
      .process(() => AccountDataSupplier(accountStateStore), accountStateStore)

    val topology = streamsBuilder.build()

    topology.addSource(InputNode, AtmRequestKeySerde.deserializer(), AtmRequestSerde.deserializer(), atmConfig.api.inputTopic)
    val processorSupplier: ProcessorSupplier[AtmRequestKey, AtmRequest] = () => TransactionHandlerProcessor(accountStateStore)
    topology.addProcessor(TransactionHandlerNode, processorSupplier, InputNode)
    topology.addSink(OutputNode, atmConfig.api.outputTopic, AtmRequestKeySerde.serializer(), AtmResponseSerde.serializer(), new AccountStreamPartitioner(), TransactionHandlerNode)
    topology.connectProcessorAndStateStores(TransactionHandlerNode,accountStateStore)

    logger.info(s"${topology.describe()}")
    topology
  }
}
