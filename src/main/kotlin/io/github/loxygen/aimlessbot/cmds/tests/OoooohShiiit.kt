package io.github.loxygen.aimlessbot.cmds.tests

import io.github.loxygen.aimlessbot.lib.commands.CommandInfo
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.abc.CommandExecutor
import io.github.loxygen.aimlessbot.lib.commands.annotations.PrefixlessCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object OoooohShiiit : CommandExecutor() {

   override val commandInfo: CommandInfo? = null

   @PrefixlessCommand(triggerRegex = "o+hshi+t")
   override fun execNoSubCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {
      event.channel.sendMessage("a rat!").queue()
      return CommandResult.SUCCESS
   }

}