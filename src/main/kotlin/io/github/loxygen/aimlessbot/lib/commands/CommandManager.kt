package io.github.loxygen.aimlessbot.lib.commands

import io.github.loxygen.aimlessbot.cmds.tests.OoooohShiiit
import io.github.loxygen.aimlessbot.cmds.tests.Ping
import io.github.loxygen.aimlessbot.lib.commands.abc.AbstractCommand
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * コマンドを司る。
 */
object CommandManager {

   /**
    * コマンドを実行するオブジェクトたち。
    */
   private val COMMANDS: List<AbstractCommand> = listOf(
      Ping,
      OoooohShiiit
   )

   /**
    * コマンドの接頭辞
    */
   const val PREFIX = "//"

   /**
    * [event] を基にコマンドを実行する。
    */
   fun executeCommand(event: MessageReceivedEvent) {

      // コマンドに関する情報をかき集める
      val doesHavePrefix = event.message.contentDisplay.startsWith(PREFIX)
      val rawText =
         event.message.contentDisplay.let { if (doesHavePrefix) it.substring(PREFIX.length) else it }
      val content = rawText.split(" ")

      if (doesHavePrefix && content[0] == "help") {
         sendHelp(event.channel)
         return
      }

      // 実行する
      val result: CommandResult = COMMANDS.find {
         it.isApplicable(if (doesHavePrefix) content[0] else event.message.contentDisplay)
      }?.runCommand(content, event)
         ?: CommandResult.UNKNOWN_MAIN_COMMAND

      // 結果に応じて処理をする
      when (result) {
         CommandResult.SUCCESS -> {
            println("[SUCCEED] by ${event.author.name}\n  ${event.message.contentDisplay}")
         }
         CommandResult.FAILED -> {
            event.channel.sendMessage("ズサーッ！(コマンドがコケた音)").queue()
            println("[FAILED] by ${event.author.name}\n  ${event.message.contentDisplay}")
         }
         CommandResult.INVALID_ARGUMENTS -> {
            event.channel.sendMessage("引数がおかしいみたいです").queue()
            println("[INVALID ARGS] by ${event.author.name}\n  ${event.message.contentDisplay}")
         }
         CommandResult.UNKNOWN_MAIN_COMMAND -> {
            if (doesHavePrefix) {
               event.channel.sendMessage("それ is 何").queue()
               println("[UNKNOWN MAIN CMD] by ${event.author.name}\n  ${event.message.contentDisplay}")
            }
         }
         CommandResult.UNKNOWN_SUB_COMMAND -> {
            event.channel.sendMessage("そのサブコマンド is 何").queue()
            println("[UNKNOWN SUB CMD] by ${event.author.name}\n  ${event.message.contentDisplay}")
         }
      }

   }

   private fun sendHelp(channel: MessageChannel) {
      channel.sendMessage(buildString {
         append("***†Flisan Aimless Bot†***\n```")
         COMMANDS.forEach {
            val info = it.commandInfo ?: return@forEach
            append("${info.name} (//${info.identify})\n")
            append("  ${info.description}\n``````")
         }
         delete(length - 3, length)
         append("各コマンドの詳細は`//<command.name>`を叩くと表示されます")
      }).queue()
   }

}