package com.example.practicemessageapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.practicemessageapp.ui.theme.PracticeMessageAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PracticeMessageAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  val hello = remember { mutableStateOf("こんにちは") }
  val context = LocalContext.current

  Column(
    modifier = modifier
  ) {
    Text(
      text = "${hello.value} $name!",
      fontSize = 32.sp, // フォントサイズを大きく
      color = Color.Red, // テキストカラーを赤くする
    )
    Button(onClick = {
      hello.value = "こんばんは"
    }) {
      Text("夜")
    } // Buttonの閉じカッコ
    Button(onClick = {
      hello.value = "おはよう"
      Toast.makeText(context, "変更しました", Toast.LENGTH_SHORT).show()
    }) {
      Text("朝")
    } // Buttonの閉じカッコ
  } // Columnの閉じカッコ
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  PracticeMessageAppTheme {
    Greeting("Android")
  }
}