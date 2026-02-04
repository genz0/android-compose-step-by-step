// 一般的なenum classの使い方
enum class TrafficSignal { // 信号機
  RED,    // 赤
  YELLOW, // 黄
  BLUE    // 青
}

// プロパティ(message)を持つenum class
enum class TrafficSignalMsg(val message: String) {
  RED("止まれ"),
  YELLOW("注意して進め"),
  BLUE("進め")
}

fun main() {
  // 値を表示する（通常のenum）
  val signal1 = TrafficSignal.RED
  println(signal1.name)  // → "RED"
  val signal2 = TrafficSignal.YELLOW
  println(signal2)       // → "YELLOW"（nameと同じ）

  println("---")

  // 値を表示する（属性付きenum）
  val signal11 = TrafficSignalMsg.RED
  println(signal11.message) // → "止まれ"
  val signal12 = TrafficSignalMsg.YELLOW
  println(signal12)         // → "？？？”（.message）を付けていない
  val signal13 = TrafficSignalMsg.BLUE
  println(signal13.message) // → "？？？"
}
