package io.github.loxygen.aimlessbot

import io.github.loxygen.aimlessbot.lib.client.Client

fun main() {
    val token = System.getenv("TOKEN") ?: error("sry i dont feel any mes")
    Client.launch(token)
}