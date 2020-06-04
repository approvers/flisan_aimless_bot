package io.github.loxygen.aimlessbot.lib.client

import io.github.loxygen.aimlessbot.lib.commands.CommandManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.properties.Delegates
import kotlin.system.exitProcess

/**
 * Discordのクライアント。
 */
object Client : ListenerAdapter() {

   /**
    * JDA実体
    */
   private lateinit var discordClient: JDA

   /**
    * Botではあるけど弾かないユーザーのID
    */
   private lateinit var userIdsWhiteList: List<Long>

   /**
    * ログを出力するチャンネル。(デバッグ等で)
    */
   private var loggingChannelId: Long by Delegates.notNull()

   /**
    * [loggingChannelId]以外のチャンネルで反応を抑制するか
    */
   private var reactOnlyLoggingChanel: Boolean by Delegates.notNull()

   /**
    * コマンドを実行する
    * @param token Discordのトークン
    */
   fun launch(token: String, clientSettingInfo: ClientSettingInfo) {

      discordClient = JDABuilder.createDefault(token)
         .setActivity(Activity.of(Activity.ActivityType.DEFAULT, "hennlo world"))
         .addEventListeners(Client)
         .build()

      loggingChannelId = clientSettingInfo.loggingChannelId
      userIdsWhiteList = clientSettingInfo.botIdsWhiteList
      reactOnlyLoggingChanel = clientSettingInfo.reactOnlyLoggingChanel

   }

   override fun onReady(event: ReadyEvent) {
      val channel = discordClient.getTextChannelById(
         loggingChannelId
      )
      if (channel == null) {
         println("--- Setting Error: Logging Channel ID ---")
         println("A channel which has Id $loggingChannelId doesn't exist!")
         println("Bot will exit with exit code 1.")
         exitProcess(1)
      }
      channel.sendMessage("***†Flisan Aimless Bot Ready†***").queue()
   }

   override fun onMessageReceived(event: MessageReceivedEvent) {

      if (event.author.isBot && !userIdsWhiteList.contains(event.author.idLong)) return
      if (reactOnlyLoggingChanel && event.channel.idLong != loggingChannelId) return

      if (event.message.contentDisplay.isEmpty()) return

      CommandManager.executeCommand(event)

   }
}
