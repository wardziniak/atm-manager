package com.wardziniak.atm.partitioners

import com.wardziniak.atm.dto.{AtmRequestKey, AtmResponse}
import org.apache.kafka.streams.processor.StreamPartitioner

/**
  * Created by wardziniak on 29.09.2020.
  */
class AccountStreamPartitioner extends StreamPartitioner[AtmRequestKey, AtmResponse] {
  override def partition(topic: String, key: AtmRequestKey, value: AtmResponse, numPartitions: Int): Integer = {
    AccountsPartitioner.basicImplementation(key.accountNumber, numPartitions)
  }
}
