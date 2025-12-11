fun main() {
  // listOfで数値のリストを作る（不変リスト）
  val numbers = listOf(1, 2, 3, 4, 5, 6)

  println("開始")
  // for文でリストの要素を順に取り出す
  for (n in numbers) {
    // もしnが2で割り切れるなら偶数
    if (n % 2 == 0) {
      println("偶数: $n")
    }
  }
  println("終了")
}
