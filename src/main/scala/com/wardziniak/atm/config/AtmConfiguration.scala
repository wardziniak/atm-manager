package com.wardziniak.atm.config

/**
  * Created by wardziniak on 28.09.2020.
  */
case class AtmConfiguration(api: Api, accountInfo: AccountInfo, applicationId: String, bootstrapServer: String, kafkaProperties: Map[String, String] = Map())

case class Api(inputTopic: String, outputTopic: String)

case class AccountInfo(accountTopic: String, accountStateStore: String)
