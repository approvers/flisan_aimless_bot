package io.github.loxygen.aimlessbot.lib.commands

import io.github.loxygen.aimlessbot.lib.commands.annotations.Argument
import io.github.loxygen.aimlessbot.lib.commands.annotations.Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.reflect.Method

/**
 * コマンドを実際に実行する機能を提供する†抽象クラス†。
 * @param identify コマンドの識別文字・。
 * @param name コマンドの名前。
 * @param description コマンドの説明。
 */
abstract class CommandExecutor(
    val identify: String,
    val name: String,
    val description: String
) {

    /**
     * サブコマンドを解析して処理を実行する。
     * @param args 分割されたコマンドのメッセージ。
     * @param event メッセージイベント。
     */
    fun parseCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {

        if(args.isEmpty()) {
            return this.execNoSubCommand(args, event)
        }

        for (method in this.getCommandMethods()) {

            // --- コマンドを選別する
            val commandAnnotation = method.getAnnotation(Command::class.java)
                ?: error("†INTERNAL ERROR†: Command Annotation Failed. getCommandMethods func is not working!!")
            if(commandAnnotation.identify != args[0]) continue

            // --- 引数の数が受け入れ可能か確認する
            val commandArgs = args.subList(1, args.size)
            val argumentAnt = method.getAnnotation(Argument::class.java)

            // 引数を取らない(@Argumentアノテーションがない)のに引数が提供されている場合は拒否
            if(argumentAnt == null && commandArgs.isNotEmpty()) return CommandResult.INVALID_ARGUMENTS
            if(argumentAnt != null) {
                // 引数を取る(@Argumentアノテーションがある)場合はアノテーションの引数によって判断する
                if(argumentAnt.denyMore && commandArgs.size > argumentAnt.count) return CommandResult.INVALID_ARGUMENTS
                if(argumentAnt.denyLess && commandArgs.size < argumentAnt.count) return CommandResult.INVALID_ARGUMENTS
            }

            return try {
                // コマンドを叩く
                method.invoke(this, commandArgs, event) as CommandResult
            } catch (e: Exception) {
                event.channel.sendMessage("ﾐ゜(`${e.javaClass.simpleName}`)\n${e.localizedMessage}だそうです").queue()
                println("-------------------")
                println("Message: " + event.message.contentDisplay)
                println("Stacktrace:")
                e.printStackTrace()
                CommandResult.FAILED
            }
        }
        return CommandResult.UNKNOWN_COMMAND
    }

    /**
     * サブコマンドが与えられなかった際に呼ばれるメソッド
     * デフォルトではヘルプが送信される
     * @param args 引数
     * @param event メッセージを受信したイベント
     */
    open fun execNoSubCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {
        return this.sendHelpText(args, event)
    }

    @Command(identify = "help", name = "ヘルプ", description = "ヘルプを表示します。")
    @Argument(count = 1, denyLess = false)
    private fun sendHelpText(args: List<String>, event: MessageReceivedEvent) : CommandResult {
        var helpText = ""
        helpText += "** --- ${this.name} (`${this.identify}`) --- **\n"
        helpText += "${this.description}\n```"

        for(method in getCommandMethods()) {
            val commandAnnotation = method.getAnnotation(Command::class.java)
                ?: error("†INTERNAL ERROR†: Command Annotation Failed. getCommandMethods func is not working!!")

            helpText += "${commandAnnotation.name} (${commandAnnotation.identify})\n"
            helpText += "  ${commandAnnotation.description}\n``````"
        }
        helpText = helpText.substring(0, helpText.length - 3)
        event.channel.sendMessage(helpText).queue()
        return CommandResult.SUCCESS
    }

    private fun getCommandMethods(): List<Method> {
        val methods: MutableList<Method> = mutableListOf()
        for (method in this.javaClass.methods) {
            if (method.returnType.name != "io.github.loxygen.aimlessbot.lib.commands.CommandResult") continue
            if (method.getAnnotation(Command::class.java) == null) continue
            methods.add(method)
        }
        return methods
    }

}