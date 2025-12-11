fun main() {
  val number1 = 4
  // ifは式なので、条件に応じた値を返せます
  val message1 = if (number1 % 2 == 0) "偶数" else "奇数"
  println("number1は${message1}")

  val number2 = 7
  val message2 = if (number2 % 2 == 0) "偶数" else "奇数"
  println("number2は${message2}")
}
