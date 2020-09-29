package com.wardziniak.atm.serdes

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.common.serialization.Serializer
import play.api.libs.json.{Json, Writes}

/**
  * Created by wardziniak on 29.09.2020.
  */
case class PlayJsonSerializer[T >: Null]()(implicit fjs: Writes[T])
  extends Serializer[T]
    with LazyLogging{
  override def serialize(topic: String, data: T): Array[Byte] = {
    Option(data)
      .map(Json.toJson[T])
      .map(_.toString())
      .map(_.getBytes)
      .orNull
  }
}
