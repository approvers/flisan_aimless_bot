package io.github.loxygen.aimlessbot.cmds.misc

import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.abc.CommandImplementer
import io.github.loxygen.aimlessbot.lib.commands.annotations.PrefixlessCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object OoooohShiiit : CommandImplementer() {

   @PrefixlessCommand(triggerWord = "ooooohshiiiit")
   override fun execNoSubCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {
      event.channel.sendMessage("a rat!").queue()
      return CommandResult.SUCCESS
   }

}