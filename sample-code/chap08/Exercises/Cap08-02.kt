fun main() {
  // let：値があれば整形、なければ既定値（null判定に使う）
  val s1: String? = "compose"
  val s2: String? = null
  println("letの結果1: ${s1?.let { it.take(3) } ?: "EMPTY"}")
  println("letの結果2: ${s2?.let { it.take(3) } ?: "EMPTY"}")

  // run：thisで参照して“値”を返す（データの加工に使う）
  val resultRun = "compose".run {
    val head = take(3)
    val len = length
    "$head($len)"  // ← take(3) と lengthをくっつけて返す
  }
  println("runの結果: $resultRun")

  // apply：設定して“同じオブジェクト”を返す（初期化などに使う）
  val resultApply = "compose".apply {
    val head = take(3)
    val len = length
    println("applyの中で: $head($len)")
  }
  println("applyの結果: $resultApply")
}
