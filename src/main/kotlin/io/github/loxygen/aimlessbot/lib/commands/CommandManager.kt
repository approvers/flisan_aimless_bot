package io.github.loxygen.aimlessbot.lib.commands

import io.github.loxygen.aimlessbot.cmds.tests.Mixed
import io.github.loxygen.aimlessbot.cmds.tests.OoooohShiiit
import io.github.loxygen.aimlessbot.cmds.tests.Ping
import io.github.loxygen.aimlessbot.lib.commands.abc.CommandExecutor
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * コマンドを司る。
 */
object CommandManager {

   /**
    * コマンドを実行するオブジェクトたち。
    */
   private val COMMAND_IMPLEMENTERS: List<CommandExecutor> = listOf(
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

      // 実行する
      var result: CommandResult = CommandResult.NOT_APPLICABLE
      for (command in COMMAND_IMPLEMENTERS) {
         result = command.executeCommand(content, event, doesHasPrefix)
         if (result != CommandResult.NOT_APPLICABLE) break
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
         CommandResult.NOT_APPLICABLE -> {
            if (doesHasPrefix) event.channel.sendMessage("それ is 何").queue()
         }
         CommandResult.UNKNOWN_SUB_COMMAND -> {
            event.channel.sendMessage("そのサブコマンド is 何").queue()
         }
      }

   }

}