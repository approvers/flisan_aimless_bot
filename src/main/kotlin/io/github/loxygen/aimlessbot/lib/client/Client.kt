package io.github.loxygen.aimlessbot.lib.client

import io.github.loxygen.aimlessbot.lib.commands.CommandManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
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
   private var loggingChannelId: Long = -1

   /**
    * コマンドを実行する
    * @param token Discordのトークン
    */
   fun launch(token: String, clientSettingInfo: ClientSettingInfo) {

      val jdaBuilder = JDABuilder.createDefault(token)
      jdaBuilder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "hennlo world"))
      jdaBuilder.addEventListeners(Client)
      discordClient = jdaBuilder.build()

      loggingChannelId = clientSettingInfo.loggingChannelId
      userIdsWhiteList = clientSettingInfo.botIdsWhiteList

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

      if ((event.author.isBot && !userIdsWhiteList.contains(event.author.idLong)) ||
         event.message.contentDisplay.isEmpty() || event.channel.idLong != loggingChannelId
      ) return

      CommandManager.executeCommand(event)

   }
}
