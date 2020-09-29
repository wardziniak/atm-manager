package com.wardziniak.atm.serdes

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.common.serialization.Deserializer
import play.api.libs.json.{Json, Reads}

/**
  * Created by wardziniak on 29.09.2020.
  */
case class PlayJsonDeserializer[T >: Null]()(implicit fjs: Reads[T]) extends Deserializer[T] with LazyLogging{
  override def deserialize(topic: String, data: Array[Byte]): T = {
    Option(data)
      .map(new String(_))
      .map(Json.parse)
      .map(Json.fromJson[T])
      .map(_.getOrElse({
        logger.error(s"Cannot parse: ${new String(data)} data: $data")
        null
      }))
      .orNull
  }
}
