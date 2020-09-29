package com.wardziniak.atm.dto

/**
  * Created by wardziniak on 28.09.2020.
  */
sealed trait RequestType

object RequestType {
  case object Deposit extends RequestType
  case object Withdraw extends RequestType
}
