package com.example.practicebooksearchapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class BookViewModel : ViewModel() {

  // 書籍検索の結果一覧
  var bookItems by mutableStateOf<List<BookItem>>(emptyList())
    private set

  // 検索キーワード
  var query by mutableStateOf("芥川龍之介")
    private set

  fun updateQuery(newQuery: String) {
    query = newQuery
  }

  // 選択した書籍
  var selectedBook by mutableStateOf<BookItem?>(null)
    private set

  // 書籍の情報をセットする関数
  fun selectBook(book: BookItem) {
    selectedBook = book
  }

  // 処理中フラグ
  var isLoading by mutableStateOf(false)
    private set

  // ダイアログ表示用メッセージ
  var message by mutableStateOf("")
    private set

  fun clearMessage() {
    message = ""
  }

  // 検索有効判定
  val isSearchEnabled get() = query.isNotEmpty() && !isLoading

  // 書籍の検索処理
  fun searchBooks() {
    viewModelScope.launch {
      isLoading = true
      try {
        val bookList = withContext(Dispatchers.IO) {

          val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
          val apiUrl =
            "https://www.googleapis.com/books/v1/volumes?q=inauthor:${encodedQuery}&maxResults=20" +
                    // APIキー利用時は↓のコメントアウトを解除し、APIキーを埋める
                    "" // + "&key=【取得したAPIキー(AIza....)】"
          // 通信用ライブラリを使って情報取得
          val client = OkHttpClient()
          val request = Request.Builder().url(apiUrl).get().build()
          val responseBody = client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
              throw Exception("HTTPステータス:${response.code}")
            }
            response.body?.string() ?: ""
          }

          // Gsonで通信結果をオブジェクト化
          val gson = Gson()
          val res = gson.fromJson(responseBody, BookInfo::class.java)
          // 取得した情報を返却する
          val bookList = res.items ?: emptyList()
          return@withContext bookList
        } // withContext(Dispatchers.IO)の閉じカッコ
        // bookItemsの値を更新
        bookItems = bookList
        // 該当データなしチェック
        if (bookItems.isEmpty()) {
          message = "該当データなし"
        }
      } catch (e: IOException) {
        message = "通信エラーが発生しました。時間をおいて再試行してください。\n${e.message}"
      } catch (e: Exception) {
        message = "書籍情報の取得に失敗しました。\n${e.message}"
      } finally {
        isLoading = false
      }

    } //viewModelScope.launchの閉じカッコ
  } // searchBooksの閉じカッコ
}