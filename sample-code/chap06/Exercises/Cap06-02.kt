enum class TrafficSignal2 { // 信号機
  RED,    // 赤
  YELLOW, // 黄
  BLUE    // 青
}

fun main() {
  // if文で分岐した例
  val signal1 = TrafficSignal2.YELLOW
  if (signal1 == TrafficSignal2.RED) {
    println("$signal1 は止まれ")
  } else if (signal1 == TrafficSignal2.YELLOW) {
    println("$signal1 は止まれ")
  } else if (signal1 == TrafficSignal2.BLUE) {
    println("$signal1 は進んでも良い")
  }

  val signal2 = TrafficSignal2.RED
  when (signal2) {
    TrafficSignal2.RED, TrafficSignal2.YELLOW -> {
      println("$signal2 は止まれ")
    }
    TrafficSignal2.BLUE -> {
      println("$signal2 は進んでも良い")
    }
  }
}
