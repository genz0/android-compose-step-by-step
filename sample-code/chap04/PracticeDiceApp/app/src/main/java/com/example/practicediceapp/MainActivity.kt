package com.example.practicediceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicediceapp.ui.theme.PracticeDiceAppTheme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PracticeDiceAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Main(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
  // サイコロの目を保持する状態変数（サイコロの目に対応する添え字）
  var dice1 by remember { mutableIntStateOf(0) }
  var dice2 by remember { mutableIntStateOf(1) }
  var dice3 by remember { mutableIntStateOf(2) }
  // 表示するメッセージを保持する状態変数
  var message by remember { mutableStateOf("") }
  // サイコロを振るきっかけとなる状態変数
  var isRolling by remember { mutableStateOf(false) }

  // サイコロ画像リソースのリスト（インデックス0〜5）
  val diceImages = remember {
    listOf(
      R.drawable.dice1,  // diceImages[0]
      R.drawable.dice2,  // diceImages[1]
      R.drawable.dice3,  // diceImages[2]
      R.drawable.dice4,  // diceImages[3]
      R.drawable.dice5,  // diceImages[4]
      R.drawable.dice6,  // diceImages[5]
    )
  }
  // LaunchedEffectはComposable内でコルーチンを起動する関数
  LaunchedEffect(isRolling) {
    if (isRolling) {
      // 10回の繰り返し処理。素早く切り替えて「振っている感じ」を出す
      repeat(10) {
        dice1 = (0..5).random()
        dice2 = (0..5).random()
        dice3 = (0..5).random()
        delay(50) // 短い待ち時間を入れて連続的に変化させる
      }
      // 表示メッセージの組み立て
      val sum = dice1 + dice2 + dice3 + 3
      val zoro = dice1 == dice2 && dice2 == dice3
      val zoroMessage = if (zoro) " ゾロ目" else ""
      message = "合計：$sum $zoroMessage"

      // 処理が終わったらfalseに戻す。これでボタンが押せるようになる
      isRolling = false
    }
  }// LaunchedEffectの閉じカッコ

  // 縦に並べるレイアウト
  Column(modifier = modifier.fillMaxSize()) {
    Spacer(modifier = Modifier.height(16.dp))
    // サイコロ画像の表示
    Row {
      Image(
        painter = painterResource(id = diceImages[dice1]),
        contentDescription = "サイコロ1",
        modifier = Modifier.weight(1f)
      )
      Image(
        painter = painterResource(id = diceImages[dice2]),
        contentDescription = "サイコロ2",
        modifier = Modifier.weight(1f)
      )
      Image(
        painter = painterResource(id = diceImages[dice3]),
        contentDescription = "サイコロ3",
        modifier = Modifier.weight(1f)
      )
    } // Rowの閉じカッコ
    Spacer(modifier = Modifier.height(16.dp))
    // メッセージの表示
    Text(
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth(),
      text = if (isRolling) "Rolling..." else message,
      fontSize = 24.sp
    )
    Spacer(modifier = Modifier.height(16.dp))
    // ボタンの表示
    Button(
      enabled = !isRolling,
      modifier = Modifier
        .align(Alignment.CenterHorizontally),
      onClick = {
        // サイコロを転がす
        isRolling = true
      },
    ) {
      Text(
        text = "サイコロを振る",
        fontSize = 24.sp
      )
    }
  } // Columnの閉じカッコ
} // Mainの閉じカッコ

@Preview(showBackground = true)
@Composable
fun MainPreview() {
  PracticeDiceAppTheme {
    Main()
  }
}
