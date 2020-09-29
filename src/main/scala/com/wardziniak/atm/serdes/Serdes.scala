package com.wardziniak.atm.serdes

import com.wardziniak.atm.dto.{AtmResponse, _}
import com.wardziniak.atm.model.Account
import julienrf.json.derived
import julienrf.json.derived.NameAdapter
import org.apache.kafka.common.serialization.Serde
import play.api.libs.json.{Json, OFormat}
import org.apache.kafka.streams.scala.{Serdes => KSerdes}

/**
  * Created by wardziniak on 29.09.2020.
  */
object Serdes {
  lazy implicit val RequestTypeFormat: OFormat[RequestType] = derived.oformat[RequestType](NameAdapter.identity)
  lazy implicit val UserWithAccountFormat: OFormat[UserWithAccount] = Json.format[UserWithAccount]
  lazy implicit val AccountFormat: OFormat[Account] = Json.format[Account]
  lazy implicit val AtmRequestFormat: OFormat[AtmRequest] = Json.format[AtmRequest]
  lazy implicit val RequestStatusFormat: OFormat[RequestStatus] = derived.oformat[RequestStatus](NameAdapter.identity)
  lazy implicit val AtmResponseFormat: OFormat[AtmResponse] = Json.format[AtmResponse]
  lazy implicit val AccountNumberSerde: Serde[AccountNumber] = KSerdes.String
  lazy implicit val AccountSerde: Serde[Account] = PlayJsonSerde[Account]()
  lazy implicit val AtmRequestKeySerde: Serde[AtmRequestKey] = PlayJsonSerde[AtmRequestKey]()
  lazy implicit val AtmRequestSerde: Serde[AtmRequest] = PlayJsonSerde[AtmRequest]()
  lazy implicit val AtmResponseSerde: Serde[AtmResponse] = PlayJsonSerde[AtmResponse]()
}
