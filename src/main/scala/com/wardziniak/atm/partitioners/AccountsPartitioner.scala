package com.wardziniak.atm.partitioners

import java.util

import com.wardziniak.atm.dto.{AccountNumber, UserWithAccount}
import org.apache.kafka.clients.producer.Partitioner
import org.apache.kafka.clients.producer.internals.DefaultPartitioner
import org.apache.kafka.common.Cluster

/**
  * Created by wardziniak on 29.09.2020.
  */
class AccountsPartitioner extends Partitioner {

  val defaultPartitioner = new DefaultPartitioner

  override def partition(topic: String, key: scala.Any, keyBytes: Array[Byte], value: scala.Any, valueBytes: Array[Byte], cluster: Cluster): Int = {
    key match {
      case accountNumber: AccountNumber =>
        AccountsPartitioner.basicImplementation(accountNumber, cluster.partitionsForTopic(topic).size)
      case userWithAccount: UserWithAccount =>
        AccountsPartitioner.basicImplementation(userWithAccount.accountNumber, cluster.partitionsForTopic(topic).size)
      case _ =>
        defaultPartitioner.partition(topic, key, keyBytes, value, valueBytes, cluster)
    }
  }

  override def close(): Unit = {}

  override def configure(configs: util.Map[String, _]): Unit = {}
}

object AccountsPartitioner {
  def basicImplementation(accountNumber: AccountNumber, numberOfPartitions: Int): Int = accountNumber.hashCode % numberOfPartitions
}