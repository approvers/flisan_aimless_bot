package io.github.loxygen.aimlessbot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.loxygen.aimlessbot.lib.client.Client
import io.github.loxygen.aimlessbot.lib.client.ClientSettingInfo
import java.io.File

fun main() {

   val settingFile = File("settings.json")
   if (!settingFile.exists()) {
      error("sry where is setting file lmfao")
   }

   val mapper = jacksonObjectMapper()
   val clientSettingInfo: ClientSettingInfo = mapper.readValue(settingFile)

   val token = System.getenv("TOKEN") ?: error("token plz?")
   Client.launch(token, clientSettingInfo)
}