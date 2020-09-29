package com.wardziniak.atm.dto

/**
  * Created by wardziniak on 28.09.2020.
  */
sealed trait RequestStatus

object RequestStatus {
  case class Rejected(reason: String) extends RequestStatus
  case object Success extends RequestStatus
}
