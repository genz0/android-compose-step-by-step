fun main() {
  val numbers = listOf(1, 2, 3)

  println("開始")

  // mapで各要素を2倍にした新しいリストを作る
  val numbers2 = numbers.map { it * 2 }

  println("元のリスト: $numbers")
  println("mapで処理したリスト: $numbers2")

  println("終了")
}
