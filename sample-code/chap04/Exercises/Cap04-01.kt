fun main() {
  var a = 3 // あとで値を変えるためにvarで宣言
  val b = 3
  val c = 3
  val and1 = (a == b) && (b == c)
  val or1 = (a == b) || (b == c)
  println("a変更前 and = ${and1}")
  println("a変更前 or  = ${or1}")

  a = 2 // varで宣言した変数aの値を変更する

  val and2 = (a == b) && (b == c)
  val or2 = (a == b) || (b == c)
  println("a変更後 and = ${and2}")
  println("a変更後 or  = ${or2}")
}
