fun main() {
  val a: Int? = 5
  val b: Int? = null

  // これはコンパイルエラーになる
  //val c = a * b
  println("開始")

  // letやifでnullチェックをする
  a?.let {
    println("aは${it * 2}")
  }

  b?.let {
    println("bは${it * 2}") // bの値はnullなのでこの行は実行されない
  }

  println("終了")
}
