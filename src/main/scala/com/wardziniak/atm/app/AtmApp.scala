package com.wardziniak.atm.app

import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.atm.logic.AtmTopologyBuilder
import com.wardziniak.atm.utils.ConfigurationLoader

/**
  * Created by wardziniak on 28.09.2020.
  */
object AtmApp extends App with LazyLogging with KafkaRunner with AtmTopologyBuilder {

  logger.info(s"Starting AtmApp")
  startApp(ConfigurationLoader.loadConfig)
}
