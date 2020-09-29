package com.wardziniak.atm.utils

import com.wardziniak.atm.config.AtmConfiguration

/**
  * Created by wardziniak on 29.09.2020.
  */
object ConfigurationLoader {

  def loadConfig = {
    pureconfig.loadConfig[AtmConfiguration]("atm-configuration").right.get
  }
}
