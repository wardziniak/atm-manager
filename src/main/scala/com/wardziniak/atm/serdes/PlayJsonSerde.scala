package com.wardziniak.atm.serdes

import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}
import play.api.libs.json.OFormat

/**
  * Created by wardziniak on 29.09.2020.
  */
case class PlayJsonSerde[T >: Null]()(implicit format: OFormat[T]) extends Serde[T]{
  override def deserializer(): Deserializer[T] = PlayJsonDeserializer[T]()

  override def serializer(): Serializer[T] = PlayJsonSerializer[T]()
}
