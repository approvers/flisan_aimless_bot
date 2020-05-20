package io.github.loxygen.aimlessbot.lib.commands

import io.github.loxygen.aimlessbot.cmds.tests.Mixed
import io.github.loxygen.aimlessbot.cmds.tests.OoooohShiiit
import io.github.loxygen.aimlessbot.cmds.tests.Ping
import io.github.loxygen.aimlessbot.lib.commands.abc.CommandExecutor
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * コマンドを司る。
 */
object CommandManager {

   /**
    * コマンドを実行するオブジェクトたち。
    */
   private val COMMAND_EXECUTORS: List<CommandExecutor> = listOf(
      Ping,
      OoooohShiiit,
      Mixed
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
      for (command in COMMAND_EXECUTORS) {
         if (command.isApplicable(
               if (doesHasPrefix) content[0] else event.message.contentDisplay,
               doesHasPrefix
            )
         ) {
            result = command.executeCommand(content, event, doesHasPrefix)
            break
         }
      }

      // 結果に応じて処理をする
      when (result) {
         CommandResult.SUCCESS -> {
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

      for (executor in COMMAND_EXECUTORS) {
         if (executor.commandInfo == null) continue
         helpText += "${executor.commandInfo!!.name} (//${executor.commandInfo!!.identify})\n"
         helpText += "  ${executor.commandInfo!!.description}\n``````"
      }
      helpText = helpText.substring(0, helpText.length - 3)
      helpText += "各コマンドの詳細は`//<command.name>`を叩くと表示されます"
      channel.sendMessage(helpText).queue()
   }

}