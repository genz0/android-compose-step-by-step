fun main() {

    // 「?.」(安全呼び出し演算子)
    val name: String? = null
    val length: Int? = name?.length // nameがnullならlengthもnull
    println("name=$name")
    println("length=$length")

    // 「?:」(エルビス演算子)
    var name2: String? = null
    var displayName = name2 ?: "nameless" // name2はnullなので"nameless"になる
    println("displayName=$displayName")
    name2 = "鈴木"
    displayName = name2 ?: "nameless" // name2は"鈴木"になる
    println("displayName=$displayName")

    // letは、nullじゃない時だけに処理を実行する
    var name3a: String? = null
    name3a?.let { println("Hello, $it") } // name3aがnullなので実行されない
    name3a = "田中"
    name3a?.let { println("Hello, $it") } // name3aがnullでないので実行される

    // letと「?:」の組み合わせ
    var name3b: String? = null
    name3b?.let { println("Hello, $it") } ?: println("nameless") // nullなので"nameless"を出力
    name3b = "null"
    name3b?.let { println("Hello, $it") } ?: println("nameless") // nullでないので"Hello, null"

    // 「!!」(非Nullアサーション)
    val name4: String? = null
    val length2 = name4!!.length // name4がnullならNullPointerException
}
