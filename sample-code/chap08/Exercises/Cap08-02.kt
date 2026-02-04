// Closeableを実装したクラスを用意。InputStream等がこの方式
class Work1 : java.io.Closeable {
  override fun close() { // use関数のスコープを抜けると自動実行する
    println("Work1:リソース開放")
  }

  fun work() {
    println("Work1:work()実行")
  }
}

// AutoCloseableを実装したクラスを用意。java.util.Scanner等がこの方式
class Work2 : AutoCloseable {
  override fun close() { // use関数のスコープを抜けると自動実行する
    println("Work2:リソース開放")
  }

  fun process(): Int {
    println("Work2:process()実行")
    return (1..6).random()
  }
}

fun main() {
  // Closeableとuseを活用する一般的な方法
  println("■パターン1 スタート")
  val w1 = Work1()
  w1.use { it.work() }

  // useを使わないならば、明示的にclose()を呼び出す必要がある（比較用）
  println("■パターン2 スタート")
  val w2 = Work1()
  try {
    w2.work()
  } finally {
    w2.close() // useと同様の効果を得るためには、try-finallyで書く必要がある
  }
  // AutoCloseableとuseを活用する一般的な方法
  println("■パターン3 スタート")
  val result = Work2().use { it.process() }
  println("result=[$result]")
}
