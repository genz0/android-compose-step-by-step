// 明示的に型を指定した例
fun main() {
  // 整数型
  val intValue: Int = 123456789 // 数値リテラル（整数）は何も付けなければInt型になる
  val longValue: Long = 1234567890123L // 数値の末尾にLを付けるとLong型になる

  // 浮動小数点型
  val floatValue: Float = 3.14f // 数値の末尾にfを付けるとFloat型になる
  val doubleValue: Double = 3.1415926535 // 小数点を含む数値は何も付けなければDouble型になる

  // 文字列型
  val stringValue: String = "Hello Kotlin" // ダブルクォートで囲んだ文字列はString型

  // 論理型
  val booleanValue: Boolean = true // trueまたはfalseを取る論理値

  // データを出力
  println("intValue: $intValue")
  println("longValue: $longValue")
  println("floatValue: $floatValue")
  println("doubleValue: $doubleValue")
  println("stringValue: $stringValue")
  println("booleanValue: $booleanValue")
}
