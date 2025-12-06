// テスト用のクラス
class Person {
    var name = "name less"
    var age = -1
}

fun main() {
    val p1 = Person()
    println("p1 name=[${p1.name}] age=[${p1.age}]")

    // applyはレシーバー(this) を利用し、オブジェクト自身を返す
    val p2 = Person().apply {
        name = "佐藤"
        age = 28
    }
    println("p2 name=[${p2.name}] age=[${p2.age}]")

    // alsoはitを使い、オブジェクト自身を返す
    val p3a = Person().also {
        it.name = "鈴木"
        it.age = 18
    }
    println("p3a name=[${p3a.name}] age=[${p3a.age}]")

    // alsoはitの代わりに引数名を明示できる
    val p3b = Person().also { arg ->
        arg.name = "鈴木b"
        arg.age = 19
    }
    println("p3b name=[${p3b.name}] age=[${p3b.age}]")

    // runは最後の式を返す(インスタンスの初期化と処理をまとめたいときに便利)
    val p4 = Person().run {
        name = "山田" // 初期化
        age = 14 // 初期化
        "p4 name=[${name}] age=[${age}]" // 初期化後に処理結果(文字列)を返す
    }
    println(p4)
}
