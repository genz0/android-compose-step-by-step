fun main() {
  // ❶ 引数も戻り値もない関数を定義して実行する
  fun lambda0() {
    println("lambda0")
  }
  lambda0()

  // ❷ ❶と同じ処理をラムダ式で表現する（型を明示したラムダ）
  val lambda1: () -> Unit = {
    println("lambda1")
  }
  lambda1()

  // ❸ ❶❷と同じ処理をラムダ式で表現する（型推論）
  val lambda2 = {
    println("lambda2")
  }
  lambda2()

  // ❹ 引数（文字列）を持つ関数を定義し、"○○さん、こんにちは" と表示する
  fun greet0(name: String) {
    println(name + " さん、こんにちは")
  }
  greet0("みな")

  // ❺ ❹をラムダ式（型推論）で書き換える
  val greet1 = { name: String ->
    println("$name さん、こんにちは")
  }
  greet1("みな")

  // ❻ 割り算をして結果を返す関数を定義する
  fun divide(arg1: Int, arg2: Int): Int {
    return arg1 / arg2
  }

  // ❼ divideを呼び出す
  println(divide(10, 2))

  // ❽ divideを名前付き引数で呼び出し、引数の順序を入れ替える
  println(divide(arg2 = 2, arg1 = 10))
}
