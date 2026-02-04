package com.example.practicebooksearchapp

const val sampleJSON = """
{
  "items": [
    {
      "volumeInfo": {
        "title": "書籍-1",
        "authors": ["著者-1"],
        "description": "詳細-1",
        "imageLinks": {
          "smallThumbnail": "smallThumbnail-1",
          "thumbnail": "thumbnail-1"
        }
      }
    },
    {
      "volumeInfo": {
        "title": "書籍-2",
        "authors": ["著者-2"],
        "description": "詳細-2",
        "imageLinks": {
          "smallThumbnail": "smallThumbnail-2",
          "thumbnail": "thumbnail-2"
        }
      }
    },
    {
      "volumeInfo": {
        "title": "書籍-3",
        "authors": ["著者-3"],
        "description": "詳細-3",
        "imageLinks": {
          "smallThumbnail": "smallThumbnail-3",
          "thumbnail": "thumbnail-3"
        }
      }
    }
  ]
}
"""


// APIレスポンス全体
data class BookInfo(
  val kind: String, // データの種類
  val totalItems: Int, // 検索結果の総数
  val items: List<BookItem>? // 書籍のリスト
)

// 1冊の本の情報
data class BookItem(
  val volumeInfo: VolumeInfo
)

// 書籍の基本的な情報
data class VolumeInfo(
  val title: String, // 書籍のタイトル
  val authors: List<String>?, // 著者のリスト
  val description: String, // 書籍の詳細
  val imageLinks: ImageLinks? // 画像リンク
) {
  // 著者名をカンマ区切りの文字列で取得する関数
  fun getAuthorsText(): String {
    return authors?.joinToString(", ") ?: ""
  }
}

//サムネイル画像の情報
data class ImageLinks(
  val smallThumbnail: String, // 小さなサムネイル画像のURL
  val thumbnail: String // サムネイル画像のURL
)
