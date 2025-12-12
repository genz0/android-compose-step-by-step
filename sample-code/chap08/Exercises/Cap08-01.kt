// 複数の関数を持つインタフェース
interface NumOp {
  fun inc(x: Int): Int
  fun dec(x: Int): Int
}

fun main() {
  // その場限りのオブジェクト。インタフェースに沿って実装
  val op = object : NumOp {
    var count = 0
    override fun inc(x: Int): Int {
      count += x
      return count
    }

    override fun dec(x: Int): Int {
      count -= x
      return count
    }
  }
  println("NumOp: inc=${op.inc(10)}")
  println("NumOp: dec=${op.dec(3)}")

  // その場限りのオブジェクト。インタフェースもなし
  val counter = object {
    var count = 0
    fun hit() {
      count++
    }
  }
  counter.hit()
  counter.hit()
  println("Counter: count=${counter.count}")
}
