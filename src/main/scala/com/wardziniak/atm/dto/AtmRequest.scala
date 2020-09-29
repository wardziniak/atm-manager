package com.wardziniak.atm.dto

/**
  * Created by wardziniak on 28.09.2020.
  */
case class AtmRequest(user: UserWithAccount, requestId: String /** unique id for whole system*/, amount: Double, requestType: RequestType)
