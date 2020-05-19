package io.github.loxygen.aimlessbot.lib.commands

/**
 * コマンドの実行結果を示す列挙体。
 */
enum class CommandResult {
   /**
    * そもそもメインコマンドが違う、または該当するPrefixlessコマンドがなかったなどで
    * そのCommandExectorでは処理できない
    */
   NOT_APPLICABLE,

   /**
    * そんなサブコマンドはない
    */
   UNKNOWN_SUB_COMMAND,

   /**
    * サブコマンドはあったけど引数の形式が受け付けられない
    */
   INVALID_ARGUMENTS,

   /**
    * 特に何もなくコマンドが成功
    */
   SUCCESS,

   /**
    * 処理に失敗した
    */
   FAILED
}
