fun main() {
  val fruits = listOf("林檎", "梨", "柿")

  println("開始 forEach")

  // forEachで要素を順に取り出す
  fruits.forEach { fruit ->
    println("フルーツ: $fruit")
  }

  println("開始 forEachIndexed")

  // forEachIndexedで要素をインデックス付きで順に取り出す
  fruits.forEachIndexed { index, fruit ->
    println("フルーツ$index: $fruit")
  }

  println("終了")
}
