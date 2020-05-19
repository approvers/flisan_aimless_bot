package io.github.loxygen.aimlessbot.lib.commands.annotations

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import io.github.loxygen.aimlessbot.lib.commands.CommandResult

/**
 * Prefixを付けすに実行するコマンドのメソッドにつけるアノテーション。
 * メソッドは以下の形式にそっている必要がある:
 * method(args: List<String>, event: [MessageReceivedEvent]) : [CommandResult]
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrefixlessCommand(
    /**
     * この正規表現にメッセージがマッチすると、コマンドが発火される。
     */
    val triggerRegex: String
)