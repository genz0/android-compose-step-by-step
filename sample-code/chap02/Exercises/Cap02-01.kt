fun main() {
  // varは再代入できる変数
  var name = "Android"

  // 文字列は＋演算子で結合することができる
  println("Hello " + name + "!") //printlnは改行のある出力

  // $変数名 で変数の値を文字列に埋め込む
  println("Hello $name!")

  // varなので再代入できる
  name = "Kotlin"
  println("Hello ${name}!")
}
