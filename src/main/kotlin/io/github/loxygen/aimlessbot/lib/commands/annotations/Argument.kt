package io.github.loxygen.aimlessbot.lib.commands.annotations

/**
 * コマンドが引数を取ることを伝えるアノテーション。
 */
annotation class Argument(
   /**
    * 引数の数。
    */
   val count: Int,
   /**
    * (default true)[count]が引数の数より小さい場合、実行を拒否する。
    */
   val denyLess: Boolean = true,
   /**
    * (default true)[count]が引数の数より大きい場合、実行を拒否する。
    */
   val denyMore: Boolean = true
)