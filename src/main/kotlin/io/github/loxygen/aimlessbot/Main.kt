package io.github.loxygen.aimlessbot

import io.github.loxygen.aimlessbot.lib.Client

fun main() {
    val token = System.getenv("TOKEN") ?: error("sry i dont feel any token")
    Client.launch(token)
}