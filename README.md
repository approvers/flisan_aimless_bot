# Flisan Aimless Bot
フライさんが書いたBotなのだ

## Aimless?
特に目的はなかったんですけど、将来的には████侍ゲームを実装したいなって思ってます

## コマンドの種類

†接頭辞ありコマンド†が頭に`//`を**付けて**実行するコマンドです<br>
†接頭辞なしコマンド†が頭に`//`を**付けないで**実行するコマンドです

### 書式

```
//接頭辞ありコマンド サブコマンド 引数...
```

## 構造

- `(なんかいっぱい)/io.github.loxygen.aimlessbot/`

  - `Main.kt`<br>
    エントリポイントです
  - `lib/`
    - `Client.kt`<br>
      クライアント本体です
    - `commands/`
      - `abc/`
        - `CommandExecutor.kt`<br>
          コマンドの***†抽象基底クラス†***です
          すべてのコマンドは`CommandExecutor`に通じる
      - `annotations/`
        - `Argument.kt`<br>
          コマンドが引数を取りたいときにつけるアノテーションです
        - `SubCommand.kt`<br>
          そのメソッドが接頭辞**あり**コマンドのサブコマンドであることを示すアノテーションです
        - `PrefixlessCommand.kt`<br>
          そのメソッドが接頭辞**なし**コマンドであることを示すアノテーションです
      - `CommandInfo.kt`<br>
        接頭辞ありコマンドの情報を保持するデータクラスです
      - `CommandManager.kt`<br>
        †すべてのコマンドを司る†クラスです
      - `CommandResult.kt`<br>
        コマンドの実行結果を示す列挙体です
  - `cmds`<br>
    ここにコマンドを増やしていくつもりでいます
    - `tests`
      - `Ping.kt` / `OoooohShiiit.kt` / `Mixed.kt`<br>
        テストに使うコマンドの実装です

## コマンドの実装の仕方

```kotlin
object Command : CommandExecutor() {

    override val commandInfo = CommandInfo(
        identify = "cmd",
        name = "コマンドの名前",
        description = "コマンドの説明"
    )

    @Command(identify = "exec", name = "サブコマンドの名前", description = "サブコマンドの説明")
    @Argument(count = 1)
    fun someNiceCommand(args: List<String>, event: MessageReceivedCommand) : CommandResult {
        /* Some nice 処理 */
        return CommandResult.SUCCESS
    }

    @PrefixlessCommand(triggerRegex = "正規表現")
    fun someNicePrefixlessCommand(args: List<String>, event: MessageReceivedCommand) : CommandResult {
        /* Some nice 処理 */
        return CommandResult.SUCCESS
    }
}
```

`@Argument` は引数を受け取らなければ書かなくてもいいです(引数を渡すとエラーになります)

## つらいのだ

IntelliJ IDEAでMarkdown編集しようとしたら文字化けしました…<br>

Typoraはいいぞ