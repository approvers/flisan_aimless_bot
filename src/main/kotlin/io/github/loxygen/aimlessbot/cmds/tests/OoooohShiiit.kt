package io.github.loxygen.aimlessbot.cmds.tests

import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.abc.PrefixlessCommandExecutor
import io.github.loxygen.aimlessbot.lib.commands.annotations.PrefixlessCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object OoooohShiiit : PrefixlessCommandExecutor() {

   @PrefixlessCommand(triggerRegex = "o+hshi+t")
   fun ooooohShiiiit(content: String, event: MessageReceivedEvent): CommandResult {
      event.channel.sendMessage("a rat!").queue()
      return CommandResult.SUCCESS
   }

}