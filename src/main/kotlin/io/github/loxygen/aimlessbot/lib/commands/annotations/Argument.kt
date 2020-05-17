package io.github.loxygen.aimlessbot.lib.commands.annotations

/**
 * コマンドが引数を取ることを伝えるアノテーション。
 * @param count 引数の数。
 * @param denyLess (default true)[count]が引数の数より小さい場合、実行を拒否する。
 * @param denyMore (default true)[count]が引数の数より大きい場合、実行を拒否する。
 */
annotation class Argument(
    val count: Int,
    val denyLess: Boolean = true,
    val denyMore: Boolean = true
)