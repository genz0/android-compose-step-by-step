fun main() {
  // ❶ 引数としてラムダ式を受け取る関数を定義（高階関数の基本形）
  fun func01(param: () -> Unit) {
    param()
  }

  // ❷ 名前付き引数を使って呼び出す
  func01(param = { println("❷ 名前付き引数として呼び出す") })

  // ❸ 名前付き引数を使わずに呼び出す（通常の位置引数の形）
  func01({ println("❸ 名前付き引数を使わずに呼び出す") })

  // ❹ トレーリングラムダ式（最後の引数がラムダなら丸括弧の外に書ける）
  func01() {
    println("❹ トレーリングラムダ式（Composableでよく使う形）")
  }

  // ❺ 引数がラムダ1つだけの場合は()も省略できる
  func01 {
    println("❺ トレーリングラムダ式＋()省略形")
  }

  // ❻ 通常の引数とラムダを受け取る関数を定義（高階関数の応用）
  fun func02(param1: String, param2: (arg: String) -> Unit) {
    param2(param1)
  }

  // ❼ 引数付きラムダのトレーリング形式（ComposableのTextFieldなどでよくある形）
  func02("トレーリングラムダ") { arg ->
    println("❼ 引数付きラムダ式：$arg")
  }
}
