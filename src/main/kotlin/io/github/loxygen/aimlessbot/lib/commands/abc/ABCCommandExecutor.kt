package io.github.loxygen.aimlessbot.lib.commands.abc

import io.github.loxygen.aimlessbot.lib.commands.CommandInfo
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class ABCCommandExecutor {

   /**
    * コマンドの情報。nullを代入するとhelpなどが無効になる
    */
   abstract val commandInfo: CommandInfo?

   /**
    * [query]を基に、このCommandBaseで対象のコマンドの処理が可能かを返す
    * @return 処理ができればtrue
    */
   abstract fun isApplicable(query: String): Boolean

   /**
    * 内部呼び出し用。
    * 与えられた[content]と[event]を基にコマンドメソッドを実行する。
    * @param content サブコマンドを含めた引数たち/
    * @param event メッセージ受診時に発火されたイベント
    * @return コマンド実行の結果
    */
   abstract fun executeCommand(content: List<String>, event: MessageReceivedEvent): CommandResult

   /**
    * コマンドを実行する
    * @param content サブコマンドを含めた引数たち
    * @param event メッセージ受診時に発火されたイベント
    * @return コマンド実行の結果
    */
   fun runCommand(content: List<String>, event: MessageReceivedEvent): CommandResult {
      return try {
         this.executeCommand(content, event)
      } catch (e: Exception) {
         event.channel.sendMessage("ﾐ゜(`${e.javaClass.simpleName}`)\n${e.localizedMessage}だそうです").queue()
         println("-------------------")
         println("Message: " + event.message.contentDisplay)
         println("Stacktrace:")
         e.printStackTrace()
         return CommandResult.FAILED
      }
   }

}