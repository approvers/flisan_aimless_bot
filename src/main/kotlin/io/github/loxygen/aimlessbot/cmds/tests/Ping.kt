package io.github.loxygen.aimlessbot.cmds.tests

import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.abc.PrefixnessCommand
import io.github.loxygen.aimlessbot.lib.commands.annotations.Argument
import io.github.loxygen.aimlessbot.lib.commands.annotations.SubCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.random.Random

object Ping : PrefixnessCommand(
   identify = "ping",
   name = "Ping",
   description = "フェイクのping値を返します。"
) {

   @SubCommand(identify = "exec", name = "実行", description = "引数をつけるとPing値がその値になります")
   @Argument(count = 1, denyLess = false)
   fun doPing(args: List<String>, event: MessageReceivedEvent): CommandResult {
      val pingMillisec = args.getOrNull(0)?.toIntOrNull() ?: Random.nextInt(10, 300000)
      event.channel.sendMessage("```PING: $pingMillisec ms```").queue()
      return CommandResult.SUCCESS
   }

   @SubCommand(identify = "pong", name = "Pong", description = "Pongを返します")
   fun doPong(args: List<String>, event: MessageReceivedEvent): CommandResult {
      event.channel.sendMessage("ＰＯＮＧ").queue()
      return CommandResult.SUCCESS
   }

}