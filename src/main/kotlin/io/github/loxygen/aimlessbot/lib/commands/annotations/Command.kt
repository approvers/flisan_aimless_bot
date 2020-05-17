package io.github.loxygen.aimlessbot.lib.commands.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(
    val identify: String,
    val name: String,
    val description: String
)