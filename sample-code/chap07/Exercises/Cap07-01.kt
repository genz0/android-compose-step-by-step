// コンパニオンオブジェクトの例
class Ticket1 {
  companion object {
    const val PREFIX = "TK-"
    fun code(id: Int): String = PREFIX + id
  }
}

// 一般的な例
class Ticket2 {
  val prefix = "TK-"
  fun code(id: Int): String = prefix + id
}

fun main() {
  // クラス名で直接呼び出し
  println("コンパニオンオブジェクト")
  println(Ticket1.code(123))

  // 一般的な例
  println("インスタンス")
  println(Ticket2().code(456))
}
