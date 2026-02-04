package com.example.practicehighlowapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicehighlowapp.ui.theme.PracticeHighLowAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PracticeHighLowAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Main(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
  // コンピュータとプレイヤーのカードの数値を保持する状態変数
  val com = remember { mutableIntStateOf((1..13).random()) }
  val you = remember { mutableIntStateOf((1..13).random()) }
  // ゲームの結果メッセージを保持する状態変数
  var message by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxSize() // 利用可能な画面のエリアすべて使う
      .background(Color(0xFF32A532)) // 背景色をやや明るめの緑色に変更
      .padding(16.dp), // 内部に余白を設ける
    horizontalAlignment = Alignment.CenterHorizontally // 子要素を水平方向に中央揃え
  ) {
    Text(text = "相手のカード", fontSize = 20.sp)
    Box( // カード表示用のBox
      modifier = Modifier
        .width(120.dp)
        .height(160.dp)
        .padding(8.dp)
        .border(2.dp, Color.Black)
        .background(Color.White),
      contentAlignment = Alignment.Center
    ) {
      // コンピュータのカードを表示
      Text(
        text = com.intValue.toString(),
        fontSize = 48.sp
      )
    } // カード表示用Boxの閉じカッコ
    Spacer(modifier = Modifier.height(24.dp)) // 要素間のスペース
    Text(text = "あなたのカード", fontSize = 20.sp)
    Box( // カード表示用のBox
      modifier = Modifier
        .width(120.dp)
        .height(160.dp)
        .padding(8.dp)
        .border(2.dp, Color.Black)
        .background(Color.White),
      contentAlignment = Alignment.Center
    ) {
      Text(
        // ゲーム中は伏せ字、結果表示時はプレイヤーのカードの数値を表示
        text = if (message.isEmpty()) "??" else you.intValue.toString(),
        fontSize = 48.sp
      ) // あなたのカードの閉じカッコ
    } // カード表示用Boxの閉じカッコ
    Spacer(modifier = Modifier.height(24.dp)) // 要素間のスペース
    if (message.isEmpty()) {  // ゲーム結果が表示されていない場合
      Row(
        modifier = Modifier.fillMaxWidth(), // 画面の横幅いっぱいを使う
        horizontalArrangement = Arrangement.SpaceEvenly, // 子要素を均等に配置
      ) {
        // 「High」ボタン
        Button(
          onClick = {
            // 勝敗判定
            if (com.intValue == you.intValue) {
              message = "引き分け"
            } else if (com.intValue < you.intValue) {
              message = "あなたの勝ち！"
            } else {
              message = "あなたの負け！"
            }
          },
        ) {
          Text(text = "High", fontSize = 20.sp)
        }// 「High」ボタンの閉じカッコ
        // 「Low」ボタン
        Button(
          onClick = {
            // 勝敗判定
            if (com.intValue == you.intValue) {
              message = "引き分け"
            } else if (com.intValue > you.intValue) {
              message = "あなたの勝ち！"
            } else {
              message = "あなたの負け！"
            }
          },
        ) {
          Text(text = "Low", fontSize = 20.sp)
        } // 「Low」ボタンの閉じカッコ
      } // Rowの閉じカッコ
    } else { // ゲーム結果が表示されている場合
      // 「次のゲーム」ボタン
      Button(
        onClick = {
          com.intValue = (1..13).random()
          you.intValue = (1..13).random()
          message = ""
        }
      ) {
        Text(text = "次のゲーム", fontSize = 20.sp)
      }
    } // elseの閉じカッコ
    Spacer(modifier = Modifier.height(24.dp)) // 要素間のスペース
    // 結果メッセージを表示
    Text(text = message, fontSize = 28.sp)
  } // Columnの閉じカッコ
} // Mainの閉じカッコ

@Preview(showBackground = true)
@Composable
fun MainPreview() {
  PracticeHighLowAppTheme {
    Main()
  }
}
