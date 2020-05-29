package io.github.loxygen.aimlessbot.lib.commands

import io.github.loxygen.aimlessbot.cmds.tests.OoooohShiiit
import io.github.loxygen.aimlessbot.cmds.tests.Ping
import io.github.loxygen.aimlessbot.lib.commands.abc.ABCCommandExecutor
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * コマンドを司る。
 */
object CommandManager {

   /**
    * コマンドを実行するオブジェクトたち。
    */
   private val COMMANDS: List<ABCCommandExecutor> = listOf(
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
      val doesHasPrefix = event.message.contentDisplay.startsWith(PREFIX)
      val rawText = event.message.contentDisplay.substring(if (doesHasPrefix) PREFIX.length else 0)
      val content = rawText.split(" ")

      if (doesHasPrefix && content[0] == "help") {
         sendHelp(event.channel)
         return
      }

      // 実行する
      var result: CommandResult = CommandResult.UNKNOWN_MAIN_COMMAND
      for (command in COMMANDS) {
         if (command.isApplicable(if (doesHasPrefix) content[0] else event.message.contentDisplay)) {
            result = command.runCommand(content, event)
            break
         }
      }

      // 結果に応じて処理をする
      when (result) {
         CommandResult.SUCCESS -> {
            println("command succeeded:\n${event.author.name}\n  ${event.message.contentDisplay}")
         }
         CommandResult.FAILED -> {
            event.channel.sendMessage("ズサーッ！(コマンドがコケた音)").queue()
         }
         CommandResult.INVALID_ARGUMENTS -> {
            event.channel.sendMessage("引数がおかしいみたいです").queue()
         }
         CommandResult.UNKNOWN_MAIN_COMMAND -> {
            if (doesHasPrefix) event.channel.sendMessage("それ is 何").queue()
         }
         CommandResult.UNKNOWN_SUB_COMMAND -> {
            event.channel.sendMessage("そのサブコマンド is 何").queue()
         }
      }

   }

   private fun sendHelp(channel: MessageChannel) {
      var helpText = "***†Flisan Aimless Bot†***\n```"

      for (command in COMMANDS) {
         if (command.commandInfo == null) continue
         helpText += "${command.commandInfo!!.name} (//${command.commandInfo!!.identify})\n"
         helpText += "  ${command.commandInfo!!.description}\n``````"
      }
      helpText = helpText.substring(0, helpText.length - 3)
      helpText += "各コマンドの詳細は`//<command.name>`を叩くと表示されます"
      channel.sendMessage(helpText).queue()
   }

}