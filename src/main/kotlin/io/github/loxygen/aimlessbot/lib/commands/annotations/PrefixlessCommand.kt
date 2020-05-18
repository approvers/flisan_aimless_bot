package io.github.loxygen.aimlessbot.lib.commands.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrefixlessCommand(
    val triggerWord: String
)