package com.example.practicebooksearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.practicebooksearchapp.ui.theme.PracticeBookSearchAppTheme


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PracticeBookSearchAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Main(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
  val viewModel: BookViewModel = viewModel()
  val navController = rememberNavController() //NavHostの状態管理
  NavHost(navController = navController, startDestination = "list") {
    composable("list") { // リスト画面
      BookList(viewModel, modifier) { item ->
        viewModel.selectBook(item)
        navController.navigate("detail")
      }
    }
    composable("detail") { // 詳細画面
      BookDetail(viewModel, modifier) {
        // 戻るボタンを押された場合の処理
        navController.popBackStack()
      }
    }
  }
  // 処理中のインジケータ表示
  if (viewModel.isLoading) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clickable { /* 何もしない */ }
        .background(Color.Black.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center,
    ) {
      // 白い円形プログレスインジケータ
      CircularProgressIndicator(color = Color.White)
    }
  }// インジケータの表示の閉じカッコ

  // メッセージの表示
  if (viewModel.message.isNotEmpty()) {
    AlertDialog(
      onDismissRequest = { viewModel.clearMessage() },
      confirmButton = {
        TextButton(onClick = { viewModel.clearMessage() }) {
          Text("OK")
        }
      },
      text = { Text(viewModel.message) }
    )
  } // メッセージの表示の閉じカッコ
} // Mainの閉じカッコ

@Composable
fun BookList(
  viewModel: BookViewModel,
  modifier: Modifier,
  onItemClick: (BookItem) -> Unit
) {

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    // 検索条件の入力と検索ボタン
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = viewModel.query,
        onValueChange = { viewModel.updateQuery(it) },
        modifier = Modifier.weight(1f),
        maxLines = 1,
        placeholder = { Text("著者名を入力") }
      )
      Spacer(modifier = Modifier.width(8.dp))
      Button(
        onClick = { viewModel.searchBooks() },
        enabled = viewModel.isSearchEnabled
      ) {
        Text("検索")
      }
    } // Rowの閉じカッコ
    Spacer(modifier = Modifier.height(16.dp))
    // 書籍のリスト表示
    LazyColumn(modifier = Modifier.padding(4.dp)) {
      items(viewModel.bookItems) { item ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
            .clickable { onItemClick(item) }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = item.volumeInfo.imageLinks?.smallThumbnail ?: "",
              contentDescription = "サムネイル画像",
              error = painterResource(R.drawable.no_image),
              placeholder = painterResource(R.drawable.loading),
              modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(4.dp)) //タイトル・著者の前に隙間をあける
            Column {
              Text(text = item.volumeInfo.title)
              Text(text = item.volumeInfo.getAuthorsText())
            }
          } // Rowの閉じカッコ
        } // Cardの閉じカッコ
      } // itemsの閉じカッコ
    } // LazyColumnの閉じカッコ
  } // Columnの閉じカッコ
} // BookListの閉じカッコ

@Composable
fun BookDetail(
  viewModel: BookViewModel,
  modifier: Modifier,
  onClick: () -> Unit
) {
  // 編集を楽にするためvolumeInfoをローカル変数で保持
  val volumeInfo = viewModel.selectedBook?.volumeInfo

  val scrollState = rememberScrollState()
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
    ) {
      Row(
        modifier = Modifier.padding(4.dp)
      ) {
        Text("書名:")
        Text(
          text = volumeInfo?.title ?: "",
        )
      }
      Row(
        modifier = Modifier.padding(4.dp)
      ) {
        Text("著者:")
        Text(
          text = volumeInfo?.getAuthorsText() ?: "",
        )
      }
    } // Columnの閉じカッコ
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = volumeInfo?.description ?: "",
      modifier = Modifier
        .verticalScroll(scrollState)
        .fillMaxWidth()
        .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
        .weight(1f)
        .padding(8.dp)
    )
    Button(
      onClick = onClick,
      modifier = Modifier.fillMaxWidth()
    ) { Text("戻る") }
  }// Columnの閉じカッコ
} // BookDetailの閉じカッコ

@Preview(showBackground = true)
@Composable
fun MainPreview() {
  PracticeBookSearchAppTheme {
    Main()
  }
}
