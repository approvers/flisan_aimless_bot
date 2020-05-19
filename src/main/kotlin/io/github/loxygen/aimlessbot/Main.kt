package io.github.loxygen.aimlessbot

import io.github.loxygen.aimlessbot.lib.Client

fun main() {
   val token = System.getenv("TOKEN") ?: error("token plz?")
   val loggingChannelId = System.getenv("LOG_CHANNEL_ID")?.toLongOrNull() ?: error("sry valid logging channel id plz?")
   Client.launch(token, loggingChannelId)
}