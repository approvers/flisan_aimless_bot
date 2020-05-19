package io.github.loxygen.aimlessbot.cmds.tests

import io.github.loxygen.aimlessbot.lib.commands.CommandInfo
import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.abc.CommandExecutor
import io.github.loxygen.aimlessbot.lib.commands.annotations.Argument
import io.github.loxygen.aimlessbot.lib.commands.annotations.SubCommand
import io.github.loxygen.aimlessbot.lib.commands.annotations.PrefixlessCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object Mixed : CommandExecutor() {

   override val commandInfo: CommandInfo? = CommandInfo(
      identify = "mix",
      name = "ミックス",
      description = "Prefix付きとなしを一つのオブジェクトで使用できるか確認したいんです(語彙力)"
   )

   @SubCommand(identify = "あ+！！+", name = "*screams*", description = "プレフィックス付きの処理です")
   @Argument(count = 0, denyLess = false, denyMore = false)
   fun chinchinWithPrefix(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      event.channel.sendMessage("うるせえ！\n引数が${args.size}個あります:\n`${args}`").queue()
      return CommandResult.SUCCESS
   }

   @PrefixlessCommand(triggerRegex = "あ+！？(！？)+")
   fun chinchinWithoutPrefix(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      event.channel.sendMessage("いきなり叫ぶな！\n引数が${args.size}個あります:\n`${args}`").queue()
      return CommandResult.SUCCESS
   }

}