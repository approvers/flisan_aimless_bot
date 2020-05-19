package io.github.loxygen.aimlessbot.lib

import io.github.loxygen.aimlessbot.lib.commands.CommandManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

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
   private val userIdsWhiteList = listOf(
      685457071906619505, /* CLIいじるこるく */
      685429240908218368, /* CLIすきすきloxy */
      684655652182032404, /* CLIに引きこもるいっそう */
      688345526181429267  /* フライさん via bot */
   )

   /**
    * コマンドを実行する
    * @param token Discordのトークン
    */
   fun launch(token: String) {

      val jdaBuilder = JDABuilder.createDefault(token)
      jdaBuilder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "hennlo world"))
      jdaBuilder.addEventListeners(Client)
      discordClient = jdaBuilder.build()

   }

   override fun onReady(event: ReadyEvent) {
      println("ready confirmed!!!")
      val channel = discordClient.getTextChannelById(695976154779222047) ?: error("sry where is channel")
      channel.sendMessage("***†Flisan Aimless Bot Ready†***").queue()
   }

   override fun onMessageReceived(event: MessageReceivedEvent) {

      if ((event.author.isBot && !userIdsWhiteList.contains(event.author.idLong)) ||
         event.message.contentDisplay.isEmpty() || event.channel.idLong != 695976154779222047
      ) {
         return
      }

      CommandManager.executeCommand(event)

   }
}
