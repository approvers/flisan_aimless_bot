package io.github.loxygen.aimlessbot.lib.client

import io.github.loxygen.aimlessbot.cmds.misc.Mixed
import io.github.loxygen.aimlessbot.cmds.misc.OoooohShiiit
import io.github.loxygen.aimlessbot.cmds.misc.Ping
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.abc.CommandImplementer
import io.github.loxygen.aimlessbot.lib.commands.annotations.Command
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * Discordのクライアント。
 */
object Client : ListenerAdapter() {

    /**
     * Prefix付きのコマンドを実行するオブジェクトたち
     */
    private val COMMAND_IMPLEMENTERS: List<CommandImplementer> = listOf(
        Ping,
        OoooohShiiit,
        Mixed
    )

    /**
     * コマンドの接頭辞
     */
    val PREFIX = "//"

    /**
     * JDA実体
     */
    private lateinit var discordClient: JDA

    /**
     * コマンドを実行する
     * @param token Discordのトークン
     */
    fun launch(token: String) {

        val jdaBuilder = JDABuilder.createDefault(token)
        jdaBuilder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "hennlo world"))
        jdaBuilder.addEventListeners(Client)
        discordClient = jdaBuilder.build()

    }

    override fun onReady(event: ReadyEvent) {
        println("ready confirmed!!!")
        val channel = discordClient.getTextChannelById(695976154779222047) ?: error("sry where is channel")
        channel.sendMessage("***†Flisan Aimless Bot Ready†***").queue()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {

        if (event.author.isBot || event.message.contentDisplay.isEmpty() || event.channel.idLong != 695976154779222047) {
            return
        }

        val doesHasPrefix = event.message.contentDisplay.startsWith(PREFIX)
        val rawText = event.message.contentDisplay.substring(if (doesHasPrefix) 2 else 0)
        val content = rawText.split(" ")

        for (command in COMMAND_IMPLEMENTERS) {

            if (doesHasPrefix && command.identify != content[0]) continue
            val result = command.parseCommand(
                content.subList(if (doesHasPrefix) 1 else 0),
                event,
                doesHasPrefix
            )

            if (!doesHasPrefix && result == CommandResult.UNKNOWN_SUB_COMMAND) continue

            when (result) {
                CommandResult.SUCCESS -> {}
                CommandResult.FAILED -> {
                    event.channel.sendMessage("ズサーッ！(コマンドがコケた音)").queue()
                }
                CommandResult.INVALID_ARGUMENTS -> {
                    event.channel.sendMessage("引数がおかしいみたいです").queue()
                }
                CommandResult.UNKNOWN_SUB_COMMAND -> {
                    event.channel.sendMessage("そのサブコマンド is 何").queue()
                }
            }
            return
        }

        if (doesHasPrefix) event.channel.sendMessage("それ is 何").queue()
    }
}

private fun <E> List<E>.subList(fromIndex: Int): List<E> {
    return this.subList(fromIndex, this.size)
}
