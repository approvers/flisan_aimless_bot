package io.github.loxygen.aimlessbot.cmds

import io.github.loxygen.aimlessbot.lib.commands.CommandExecutor
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object OoooohShiiit : CommandExecutor(
    identify = "ooooohshiiiit",
    name = "ｵｰｯ！ｼｯ！(ｶｻｶｻｶｻ)ｱﾗｰｯ！",
    description = "ｾﾞｧｰﾗﾏｻﾞｰﾌｧｯｷﾝﾗｯ!"
) {

   override fun execNoSubCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {
      event.channel.sendMessage("a rat!").queue()
      return CommandResult.SUCCESS
   }

}