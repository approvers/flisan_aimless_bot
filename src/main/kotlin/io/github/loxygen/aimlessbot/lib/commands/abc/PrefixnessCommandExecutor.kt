package io.github.loxygen.aimlessbot.lib.commands.abc

import io.github.loxygen.aimlessbot.lib.commands.CommandInfo
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.annotations.Argument
import io.github.loxygen.aimlessbot.lib.commands.annotations.SubCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.reflect.Method
import kotlin.math.min

/**
 * 接頭辞つきコマンドを実際に実行する機能を提供する†抽象クラス†。
 * あらゆる接頭辞つきコマンドはこれを継承してください
 */
abstract class PrefixnessCommandExecutor(
   identify: String,
   name: String,
   description: String
) : ABCCommandExecutor() {

   override val commandInfo: CommandInfo? = CommandInfo(
      identify, name, description
   )

   /**
    * Prefixfulコマンドを実装するメソッドとそれについてるアノテーションのキャッシュ
    */
   private val prefixfulMethodCache: List<Pair<Method, SubCommand>>

   init {
      // メソッドは変わらないし毎回リフレクションゴリゴリするの嫌なので(個人の感想)
      // ここでメソッドとアノテーションをキャッシュしてしまいます
      val prefixfulMethods: MutableList<Pair<Method, SubCommand>> = mutableListOf()

      for (method in this.javaClass.methods) {
         if (method.returnType.name != "io.github.loxygen.aimlessbot.lib.commands.CommandResult") continue

         val commandAnt = method.getAnnotation(SubCommand::class.java)

         if (commandAnt != null) {
            prefixfulMethods.add(Pair(method, commandAnt))
         }
      }

      prefixfulMethodCache = prefixfulMethods.toList()
   }

   override fun isApplicable(query: String): Boolean {
      return query == this.commandInfo?.identify
   }

   /**
    * コマンドを解析して処理を実行する。
    * @param content 分割されたコマンドのメッセージ。
    * @param event メッセージイベント。
    */
   override fun executeCommand(content: List<String>, event: MessageReceivedEvent): CommandResult {

      val subCommandContent = content.subList(1, content.size)
      val searchQuery = if (subCommandContent.isNotEmpty()) subCommandContent[0] else ""

      // 実行対象のメソッドを取得する
      val method = try {
         fetchSubCommandMethodToRun(
            searchQuery,
            subCommandContent.size - 1
         )
      } catch (e: IllegalArgumentException) {
         return CommandResult.INVALID_ARGUMENTS
      } ?: return CommandResult.UNKNOWN_SUB_COMMAND

      // メソッドを叩いて実行結果を返す
      return method.invoke(
         this,
         content.subList(min(content.size, 2), content.size),
         event
      ) as CommandResult
   }

   /**
    * 実行対象の接頭辞付きコマンドを実装しているメソッドを返す
    * @param identify サブコマンドの識別文字
    * @param argCount 与えられた引数の数
    */
   private fun fetchSubCommandMethodToRun(identify: String, argCount: Int): Method? {

      // サブコマンドが与えられていなければexecNoSubCommand()を返す
      if (identify == "") {
         return this.javaClass.getMethod("execSingleCommand", List::class.java, MessageReceivedEvent::class.java)
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
    * サブコマンドが与えられなかった際に呼ばれるメソッド
    * デフォルトではヘルプが送信される
    * @param args 引数
    * @param event メッセージを受信したイベント
    */
   open fun execSingleCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {
      return this.sendHelpText(args, event)
   }

   /**
    * ヘルプコマンド。
    */
   @SubCommand(identify = "help", name = "ヘルプ", description = "ヘルプを表示します。")
   @Argument(count = 0, denyLess = false, denyMore = false)
   fun sendHelpText(args: List<String>, event: MessageReceivedEvent): CommandResult {
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