// 可変長引数(vararg)の例
fun joinTitles(vararg titles: String): String =
  "[" + titles.joinToString(" / ")  + "]"

fun main() {
  // 文字列を渡す
  println(joinTitles("A", "B", "C")) // [A / B / C]

  // 引数なしで呼び出す
  println(joinTitles()) // []

  // 配列を渡す（スプレッド演算子 * を使用）
  val xy = arrayOf("X", "Y")
  println(joinTitles(*xy)) // [X / Y]
}
