fun main() {
  val items = mutableListOf("A", "B", "C")
  println("出力1回目：${items}")
  items.add("D")
  println("出力2回目：${items}")
  items.removeAt(1)
  println("出力3回目：${items}")
}
