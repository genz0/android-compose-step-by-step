// 明示的に型を指定しない例（型推論を使う） 
fun main() {
  // 整数型（型指定なし）
  val intValue = 123456789       // Intとして推論される
  val longValue = 1234567890123L // Longとして推論される

  // 浮動小数点型
  val floatValue = 3.14f         // Floatとして推論される
  val doubleValue = 3.1415926535 // Doubleとして推論される

  // 文字列型
  val stringValue = "Hello Kotlin" // Stringとして推論される

  // 論理型
  val booleanValue = true         // Booleanとして推論される

  // データを出力
  println("intValue: $intValue")
  println("longValue: $longValue")
  println("floatValue: $floatValue")
  println("doubleValue: $doubleValue")
  println("stringValue: $stringValue")
  println("booleanValue: $booleanValue")
}
