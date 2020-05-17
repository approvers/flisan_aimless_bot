package io.github.loxygen.aimlessbot.lib.client

import io.github.loxygen.aimlessbot.cmds.OoooohShiiit
import io.github.loxygen.aimlessbot.lib.commands.CommandExecutor
import io.github.loxygen.aimlessbot.cmds.Ping
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * Discordのクライアント。
 */
object Client : ListenerAdapter(){

    /**
     * Prefix付きのコマンドを実行するオブジェクトたち
     */
    val commandExecutors: List<CommandExecutor> = listOf(
        Ping
    )
    /**
     * プレフィックスがなくても発火するコマンドを実行するオブジェクトたち
     */
    val prefixlessCommandExecutor: List<CommandExecutor> = listOf(
        OoooohShiiit
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

        if (event.author.isBot) {
            return
        }

        var rawText = event.message.contentDisplay
        var commandExecutorList: List<CommandExecutor> = prefixlessCommandExecutor
        val prefixness = event.message.contentDisplay.startsWith(PREFIX)
        if(prefixness) {
            rawText = event.message.contentDisplay.substring(PREFIX.length)
            commandExecutorList = commandExecutors
        }
        val commandArgs = rawText.split(" ")

        for(command in commandExecutorList) {
            val isCommandMatched = (prefixness && commandArgs[0] == command.identify) ||
                                           (!prefixness && commandArgs[0].indexOf(command.identify) != -1)
            if (isCommandMatched) {
                when(command.parseCommand(commandArgs.subList(1, commandArgs.size), event)) {
                    CommandResult.SUCCESS -> {}
                    CommandResult.FAILED -> {event.channel.sendMessage("ズサーッ！(コマンドがコケた音)").queue()}
                    CommandResult.INVALID_ARGUMENTS -> {event.channel.sendMessage("引数がおかしいみたいです").queue()}
                    CommandResult.UNKNOWN_COMMAND -> {event.channel.sendMessage("そのサブコマンド is 何").queue()}
                }
                return
            }
        }

        if(prefixness) event.channel.sendMessage("それ is 何").queue()
    }

}