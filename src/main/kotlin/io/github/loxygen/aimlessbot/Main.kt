package io.github.loxygen.aimlessbot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.loxygen.aimlessbot.lib.client.Client
import io.github.loxygen.aimlessbot.lib.client.ClientSettingInfo
import java.nio.file.Files
import java.nio.file.Paths

fun main() {

   val settingFilePath = Paths.get("settings.json")
   if (!Files.exists(settingFilePath)) {
      error("sry where is setting file lmfao")
   }

   val fileContents = Files.readAllLines(settingFilePath).joinToString(separator = "")

   val mapper = jacksonObjectMapper()
   val clientSettingInfo: ClientSettingInfo = mapper.readValue(fileContents)

   val token = System.getenv("TOKEN") ?: error("token plz?")
   Client.launch(token, clientSettingInfo)
}