fun main() {
  var flag = false // 最初はfalse

  fun sample1() {
    try {
      flag = true
      println("処理中...")
      return // ここで関数を抜ける
    } finally {
      flag = false
      println("後片付け処理を実行") // 必ず実行される
    }
  }

  sample1()
  println("処理後のflag = $flag") // falseに戻っている
}
