fun main() {
  // セーフコール（safe call）
  val s1: String? = null
  println("s1=[${s1?.length}]")
  val s2: String? = "abc"
  println("s2=[${s2?.length}]")

  // エルビス演算子（Elvis operator）
  val s3: String? = null
  println("s3=[${s3 ?: "EMPTY"}]")
  val s4: String? = "abc"
  println("s4=[${s4 ?: "EMPTY"}]")

  // 非nullアサーション
  try {
    val s5: String? = "abc"
    println("s5.length=[${s5!!.length}]")
    val s6: String? = null
    println("s6.length=[${s6!!.length}]")
  } catch (e: NullPointerException) {
    println("NullPointerException 発生")
  }
}
