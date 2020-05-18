package io.github.loxygen.aimlessbot.lib.commands.abc

import io.github.loxygen.aimlessbot.lib.commands.CommandResult
import io.github.loxygen.aimlessbot.lib.commands.annotations.Argument
import io.github.loxygen.aimlessbot.lib.commands.annotations.Command
import io.github.loxygen.aimlessbot.lib.commands.annotations.PrefixlessCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.reflect.Method

/**
 * コマンドを実際に実行する機能を提供する†抽象クラス†。
 * @param identify コマンドの識別文字・。
 * @param name コマンドの名前。
 * @param description コマンドの説明。
 */
abstract class CommandImplementer(
    val identify: String? = null,
    val name: String = "",
    val description: String = ""
) {

    /**
     * サブコマンドを解析して処理を実行する。
     * @param args 分割されたコマンドのメッセージ。
     * @param event メッセージイベント。
     * @param hasPrefix プレフィックスが付けられて実行されたか否か
     */
    fun parseCommand(args: List<String>, event: MessageReceivedEvent, hasPrefix: Boolean): CommandResult {

        // サブコマンドがない
        if(hasPrefix && args.isEmpty()) {
            return this.execNoSubCommand(args, event)
        }

        // 実行対象のメソッドを取得する
        val method = try {
            if(hasPrefix)
                fetchCommandMethodToRun(args[0], args.size - 1)
            else
                fetchPrefixlessCommandMethodToRun(args[0])
        } catch (e: IllegalArgumentException) {
            return CommandResult.INVALID_ARGUMENTS
        } ?: return CommandResult.UNKNOWN_SUB_COMMAND

        return try {
            // コマンドを叩く
            method.invoke(this, args.subList(1, args.size), event) as CommandResult
        } catch (e: Exception) {
            event.channel.sendMessage("ﾐ゜(`${e.javaClass.simpleName}`)\n${e.localizedMessage}だそうです").queue()
            println("-------------------")
            println("Message: " + event.message.contentDisplay)
            println("Stacktrace:")
            e.printStackTrace()
            CommandResult.FAILED
        }
    }

    private fun fetchCommandMethodToRun(identify: String, argCount: Int): Method? {

        for (method in this.getCommandMethods(true)) {

            // --- コマンドを選別する
            val commandAnnotation = method.getAnnotation(Command::class.java)
                ?: error("†INTERNAL ERROR†: Command Annotation Failed. getCommandMethods func is not working!!")
            if (commandAnnotation.identify != identify) continue

            // --- 引数の数が受け入れ可能か確認する
            val argumentAnt = method.getAnnotation(Argument::class.java)

            // 引数を取らない(@Argumentアノテーションがない)のに引数が提供されている場合は拒否
            if (argumentAnt == null && argCount > 0) throw IllegalArgumentException()
            if (argumentAnt != null) {
                // 引数を取る(@Argumentアノテーションがある)場合はアノテーションの引数によって判断する
                if (argumentAnt.denyMore && argCount > argumentAnt.count) throw IllegalArgumentException()
                if (argumentAnt.denyLess && argCount < argumentAnt.count) throw IllegalArgumentException()
            }

            return method
        }
        return null
    }

    private fun fetchPrefixlessCommandMethodToRun(identify: String): Method? {
        for (method in this.getCommandMethods(false)) {
            val commandAnnotation = method.getAnnotation(PrefixlessCommand::class.java)
                ?: error("†INTERNAL ERROR†: Command Annotation Failed. getCommandMethods func is not working!!")
            if(commandAnnotation.triggerWord != identify) continue
            return method
        }
        return null
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

        for(method in getCommandMethods(true)) {
            val commandAnnotation = method.getAnnotation(Command::class.java)
                ?: error("†INTERNAL ERROR†: Command Annotation Failed. getCommandMethods func is not working!!")

            helpText += "${commandAnnotation.name} (${commandAnnotation.identify})\n"
            helpText += "  ${commandAnnotation.description}\n``````"
        }
        helpText = helpText.substring(0, helpText.length - 3)
        event.channel.sendMessage(helpText).queue()
        return CommandResult.SUCCESS
    }

    private fun getCommandMethods(hasPrefix: Boolean): List<Method> {
        val methods: MutableList<Method> = mutableListOf()
        for (method in this.javaClass.methods) {
            if (method.returnType.name != "io.github.loxygen.aimlessbot.lib.commands.CommandResult") continue

            val annotationClass = if(hasPrefix) Command::class.java else PrefixlessCommand::class.java
            if (method.getAnnotation(annotationClass) == null) continue
            methods.add(method)
        }
        return methods
    }

}