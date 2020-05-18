package io.github.loxygen.aimlessbot.cmds.misc

import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.abc.CommandImplementer
import io.github.loxygen.aimlessbot.lib.commands.annotations.Argument
import io.github.loxygen.aimlessbot.lib.commands.annotations.Command
import io.github.loxygen.aimlessbot.lib.commands.annotations.PrefixlessCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object Mixed : CommandImplementer(
    identify = "mix",
    name = "ミックス",
    description = "Prefix付きとなしを一つのオブジェクトで使用できるか確認したいんです(語彙力)"
) {

   @Command(identify = "あああああああああああああ！！！！", name = "*screams*", description = "プレフィックス付きの処理です")
   @Argument(count = 0, denyLess = false, denyMore = false)
   fun chinchinWithPrefix(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      event.channel.sendMessage("うるせえ！\n引数が${args.size}個あります:\n`${args}`").queue()
      return CommandResult.SUCCESS
   }

   @PrefixlessCommand(triggerWord = "あああああああああああああ！？！？")
   fun chinchinWithoutPrefix(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      event.channel.sendMessage("いきなり叫ぶな！\n引数が${args.size}個あります:\n`${args}`").queue()
      return CommandResult.SUCCESS
   }

}