package com.wardziniak.atm.logic

import com.wardziniak.atm.dto.AccountNumber
import com.wardziniak.atm.model.Account
import org.apache.kafka.streams.processor.{Processor, ProcessorContext}
import org.apache.kafka.streams.state.KeyValueStore


/**
  * Created by wardziniak on 29.09.2020.
  */
case class AccountDataSupplier(accountStoreName: String) extends Processor[AccountNumber, Account] {

  private var accountStore: KeyValueStore[AccountNumber, Account] = _

  override def init(context: ProcessorContext): Unit = {
    accountStore = context.getStateStore(accountStoreName).asInstanceOf[KeyValueStore[AccountNumber, Account]]
  }

  override def process(accountNumber: AccountNumber, account: Account): Unit = {
    accountStore.put(accountNumber, account)
  }

  override def close(): Unit = {}
}
