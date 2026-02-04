// valとvarの違い
fun main() {
  // 文字列変数「str1」をvalで宣言し、初期値「abc」を設定する
  val str1 = "abc"
  // str1の値を出力する
  println(str1)
  // str1の文字列の長さを出力する
  println(str1.length)
  // str1に「xyz」を代入しようとする（エラーになる）
  str1 = "xyz" // ❶ コンパイルエラーになることを確認したら、この行をコメントアウトする
  // 文字列変数「str2」をvarで宣言し、初期値「def」を設定する
  var str2 = "def"
  // str2に「xyz」を代入し、出力する
  str2 = "xyz"
  println(str2)
}
