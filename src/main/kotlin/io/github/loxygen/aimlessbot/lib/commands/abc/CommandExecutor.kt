package io.github.loxygen.aimlessbot.lib.commands.abc

import io.github.loxygen.aimlessbot.lib.commands.CommandInfo
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.annotations.Argument
import io.github.loxygen.aimlessbot.lib.commands.annotations.PrefixlessCommand
import io.github.loxygen.aimlessbot.lib.commands.annotations.SubCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.reflect.Method
import kotlin.math.min

/**
 * コマンドを実際に実行する機能を提供する†抽象クラス†。
 * あらゆるコマンドはこれを継承してください
 */
abstract class CommandExecutor {

   /**
    * コマンドの情報。Helpに入ってきます
    * Prefixfulコマンドがある場合はちゃんと書いてください ←これここに書くのよくないよな
    */
   abstract val commandInfo: CommandInfo?

   /**
    * Prefixfulコマンドを実装するメソッドとそれについてるアノテーションのキャッシュ
    */
   private val prefixfulMethodCache : List<Pair<Method, SubCommand>>

   /**
    * Prefixfulコマンドを実装するメソッドとそれについてるアノテーションのキャッシュ
    */
   private val prefixlessMethodCache : List<Pair<Method, PrefixlessCommand>>

   init {
      // メソッドは変わらないし毎回リフレクションゴリゴリするの嫌なので(個人の感想)
      // ここでメソッドとアノテーションをキャッシュしてしまいます
      val prefixfulMethods : MutableList<Pair<Method, SubCommand>> = mutableListOf()
      val prefixlessMethods : MutableList<Pair<Method, PrefixlessCommand>> = mutableListOf()

      for(method in this.javaClass.methods) {
         if (method.returnType.name != "io.github.loxygen.aimlessbot.lib.commands.CommandResult") continue

         val commandAnt = method.getAnnotation(SubCommand::class.java)
         val prefixlessCommandAnt = method.getAnnotation(PrefixlessCommand::class.java)

         if(commandAnt != null) {
            prefixfulMethods.add(Pair(method, commandAnt))
         }
         if(prefixlessCommandAnt != null) {
            prefixlessMethods.add(Pair(method, prefixlessCommandAnt))
         }
      }

      prefixfulMethodCache = prefixfulMethods.toList()
      prefixlessMethodCache = prefixlessMethods.toList()

   }

   /**
    * コマンドを解析して処理を実行する。
    * @param content 分割されたコマンドのメッセージ。
    * @param event メッセージイベント。
    * @param hasPrefix プレフィックスが付けられて実行されたか否か
    */
   fun executeCommand(content: List<String>, event: MessageReceivedEvent, hasPrefix: Boolean): CommandResult {

      // コマンドが違ったら帰る
      if (hasPrefix && content[0] != this.commandInfo?.identify) return CommandResult.NOT_APPLICABLE

      // 実行対象のメソッドを取得する
      val method = try {
         fetchSubCommandMethodToRun(content.subList(if (hasPrefix) 1 else 0, content.size), hasPrefix)
      } catch (e: IllegalArgumentException) {
         return CommandResult.INVALID_ARGUMENTS
      } ?: return if(hasPrefix) CommandResult.UNKNOWN_SUB_COMMAND else CommandResult.NOT_APPLICABLE

      // メソッドを叩いて実行結果を返す
      return try {
         method.invoke(
            this,
            content.subList(min(content.size, if (hasPrefix) 2 else 1), content.size),
            event
         ) as CommandResult
      } catch (e: Exception) {
         event.channel.sendMessage("ﾐ゜(`${e.javaClass.simpleName}`)\n${e.localizedMessage}だそうです").queue()
         println("-------------------")
         println("Message: " + event.message.contentDisplay)
         println("Stacktrace:")
         e.printStackTrace()
         CommandResult.FAILED
      }
   }

   /**
    * 実行対象のコマンドを実装しているメソッドを返す
    * @param args 引数
    * @param hasPrefix プレフィックス付きで実行されたか
    */
   private fun fetchSubCommandMethodToRun(args: List<String>, hasPrefix: Boolean): Method? {
      return if (hasPrefix)
         fetchPrefixfulCommandMethodToRun(if (args.isNotEmpty()) args[0] else "", args.size - 1)
      else
         fetchPrefixlessCommandMethodToRun(args[0])
   }

   /**
    * 実行対象の接頭辞付きコマンドを実装しているメソッドを返す
    * @param identify サブコマンドの識別文字
    * @param argCount 与えられた引数の数
    */
   private fun fetchPrefixfulCommandMethodToRun(identify: String, argCount: Int): Method? {

      // サブコマンドが与えられていなければexecNoSubCommand()を返す
      if (identify == "") {
         return this.javaClass.getMethod("execNoSubCommand", List::class.java, MessageReceivedEvent::class.java)
      }

      for (method in this.prefixfulMethodCache) {

         if (method.second.identify != identify) continue

         // --- 引数の数が受け入れ可能か確認する
         val argumentAnt = method.first.getAnnotation(Argument::class.java)

         // 引数を取らない(@Argumentアノテーションがない)のに引数が提供されている場合は拒否
         if (argumentAnt == null && argCount > 0) throw IllegalArgumentException()
         if (argumentAnt != null) {
            // 引数を取る(@Argumentアノテーションがある)場合はアノテーションの引数によって判断する
            if (argumentAnt.denyMore && argCount > argumentAnt.count) throw IllegalArgumentException()
            if (argumentAnt.denyLess && argCount < argumentAnt.count) throw IllegalArgumentException()
         }

         return method.first
      }
      return null
   }


   /**
    * 実行対象の接頭辞なしコマンドを実装しているメソッドを返す
    * @param identify サブコマンドの識別文字
    */
   private fun fetchPrefixlessCommandMethodToRun(identify: String): Method? {
      for (method in this.prefixlessMethodCache) {
         if (!Regex(method.second.triggerRegex).containsMatchIn(identify)) continue
         return method.first
      }
      return null
   }

   /**
    * サブコマンドが与えられなかった際に呼ばれるメソッド
    * デフォルトではヘルプが送信される
    * @param args 引数
    * @param event メッセージを受信したイベント
    */
   open fun execNoSubCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {
      return this.sendHelpText(args, event)
   }

   /**
    * ヘルプコマンド。
    */
   @SubCommand(identify = "help", name = "ヘルプ", description = "ヘルプを表示します。")
   @Argument(count = 0, denyLess = false, denyMore = false)
   private fun sendHelpText(args: List<String>, event: MessageReceivedEvent): CommandResult {
      var helpText = ""
      helpText += "** --- ${this.commandInfo!!.name} (`${this.commandInfo!!.identify}`) --- **\n"
      helpText += "${this.commandInfo!!.description}\n```"

      for (method in this.prefixfulMethodCache) {
         helpText += "${method.second.name} (${method.second.identify})\n"
         helpText += "  ${method.second.description}\n``````"
      }
      helpText = helpText.substring(0, helpText.length - 3)
      event.channel.sendMessage(helpText).queue()
      return CommandResult.SUCCESS
   }

}