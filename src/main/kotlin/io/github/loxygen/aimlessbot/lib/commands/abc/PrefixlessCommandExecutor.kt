package io.github.loxygen.aimlessbot.lib.commands.abc

import io.github.loxygen.aimlessbot.lib.commands.CommandInfo
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.annotations.PrefixlessCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.reflect.Method

/**
 * 接頭辞なしコマンドを実際に実行する機能を提供する†抽象クラス†。
 * あらゆる接頭辞なしコマンドはこれを継承してください
 */
abstract class PrefixlessCommandExecutor : ABCCommandExecutor() {

   override val commandInfo: CommandInfo? = null

   /**
    * Prefixlessコマンドを実装するメソッドとそれについてるアノテーションのキャッシュ
    */
   private val prefixlessMethodCache: List<Pair<Method, PrefixlessCommand>>

   init {
      // メソッドは変わらないし毎回リフレクションゴリゴリするの嫌なので(個人の感想)
      // ここでメソッドとアノテーションをキャッシュしてしまいます
      val prefixlessMethods: MutableList<Pair<Method, PrefixlessCommand>> = mutableListOf()

      for (method in this.javaClass.methods) {
         if (method.returnType.name != "io.github.loxygen.aimlessbot.lib.commands.CommandResult") continue

         val commandAnt = method.getAnnotation(PrefixlessCommand::class.java)

         if (commandAnt != null) {
            prefixlessMethods.add(Pair(method, commandAnt))
         }
      }

      prefixlessMethodCache = prefixlessMethods.toList()
   }

   override fun isApplicable(query: String): Boolean {
      return this.prefixlessMethodCache.find { Regex(it.second.triggerRegex).containsMatchIn(query) } != null
   }

   /**
    * コマンドを解析して処理を実行する。
    * @param content 分割されたコマンドのメッセージ。
    * @param event メッセージイベント。
    */
   override fun executeCommand(content: List<String>, event: MessageReceivedEvent): CommandResult {

      // 実行対象のメソッドを取得する
      val method = fetchSubCommandMethodToRun(event.message.contentDisplay)
         ?: return CommandResult.UNKNOWN_SUB_COMMAND

      // メソッドを叩いて実行結果を返す
      return method.invoke(
         this,
         event.message.contentDisplay,
         event
      ) as CommandResult
   }

   /**
    * 実行対象の接頭辞付きコマンドを実装しているメソッドを返す
    * @param content メッセージの中身
    */
   private fun fetchSubCommandMethodToRun(content: String): Method? {
      for (method in this.prefixlessMethodCache) {
         if (!Regex(method.second.triggerRegex).containsMatchIn(content)) continue
         return method.first
      }
      return null
   }

}