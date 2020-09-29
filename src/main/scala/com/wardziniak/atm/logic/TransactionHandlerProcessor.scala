package com.wardziniak.atm.logic

import com.wardziniak.atm.dto.RequestStatus.{Rejected, Success}
import com.wardziniak.atm.dto.RequestType.{Deposit, Withdraw}
import com.wardziniak.atm.dto.{AccountNumber, AtmRequest, AtmRequestKey, AtmResponse}
import com.wardziniak.atm.model.Account
import org.apache.kafka.streams.processor.{Processor, ProcessorContext}
import org.apache.kafka.streams.state.KeyValueStore

/**
  * Created by wardziniak on 29.09.2020.
  */
case class TransactionHandlerProcessor(accountStoreName: String) extends Processor[AtmRequestKey, AtmRequest]{

  private var accountStore: KeyValueStore[AccountNumber, Account] = _
  private var context: ProcessorContext = _

  override def init(context: ProcessorContext): Unit = {
    this.context = context
    accountStore = context.getStateStore(accountStoreName).asInstanceOf[KeyValueStore[AccountNumber, Account]]
  }

  override def process(key: AtmRequestKey, request: AtmRequest): Unit = {
    val atmResponse = (request.requestType, request.amount, Option.apply(accountStore.get(key.accountNumber))) match {
      case (_, _, None) => AtmResponse(requestId = request.requestId, status = Rejected(s"Unknown Account ${key.accountNumber}"))
      case (_, amount, Some(_)) if amount < 0 => AtmResponse(requestId = request.requestId, status = Rejected(s"Amount cannot be negative"))
      case (Withdraw, amount, Some(account)) if account.balance - amount < 0 => AtmResponse(requestId = request.requestId, status = Rejected(s"Balance cannot drop below zero, current balance: ${account.balance}"))
      case (Withdraw, amount, Some(account)) =>
        accountStore.put(key.accountNumber, account.copy(balance = account.balance - amount))
        AtmResponse(requestId = request.requestId, status = Success)
      case (Deposit, amount, Some(account)) =>
        accountStore.put(key.accountNumber, account.copy(balance = account.balance + amount))
        AtmResponse(requestId = request.requestId, status = Success)
      case (_, _, _) => AtmResponse(requestId = request.requestId, status = Rejected(s"Unknown operation $request"))
    }
    context.forward[AtmRequestKey, AtmResponse](request.user, atmResponse)
  }

  override def close(): Unit = {}
}
