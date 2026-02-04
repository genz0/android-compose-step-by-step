package com.example.practicepianoapp

import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.AudioAttributes.USAGE_GAME
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.practicepianoapp.ui.theme.PracticePianoAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PracticePianoAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Main(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  // 音声リソースのリスト(白鍵盤)
  val whiteRawIds = remember {
    listOf(R.raw.c1, R.raw.d1, R.raw.e1, R.raw.f1, R.raw.g1, R.raw.a1, R.raw.b1, R.raw.c2)
  }
  //  音声リソースのリスト(黒鍵盤)
  val blackRawIds = remember {
    listOf(R.raw.c1s, R.raw.d1s, null, R.raw.f1s, R.raw.g1s, R.raw.a1s, null)
  }

  // SoundPoolの準備
  val soundPool = remember {
    SoundPool.Builder()
      .setMaxStreams(10)
      .setAudioAttributes(
        AudioAttributes.Builder()
          .setUsage(USAGE_GAME)
          .setContentType(CONTENT_TYPE_MUSIC)
          .build()
      )
      .build()
  }
  // SoundPoolに読み込んだIDの保存先
  val whiteKeys = remember { mutableStateListOf<Int>() }
  val blackKeys = remember { mutableStateListOf<Int?>() }

  // 音声リソースをSoundPoolに読み込む（非同期ロード処理
  LaunchedEffect(Unit) {
    whiteRawIds.forEach { id ->
      whiteKeys.add(soundPool.load(context, id, 1))
    }
    blackRawIds.forEach { id ->
      if (id != null) {
        blackKeys.add(soundPool.load(context, id, 1))
      } else {
        blackKeys.add(null)
      }
    }
  }

  // MediaPlayerの解放処理
  // このComposableが使われなくなったときに呼び出される
  DisposableEffect(Unit) {
    onDispose {
      soundPool.release()
    }
  }

  // 領域すべてを囲うBox（後で黒鍵盤を上に重ねるための土台）
  Box(modifier = modifier.fillMaxSize()) {
    // 白鍵盤全体
    Row(modifier = Modifier.fillMaxSize()) {
      whiteKeys.forEach { soundId -> // 各キーに対応するsoundId
        KeyBox( // 鍵盤1つ分
          modifier = Modifier
            .padding(1.dp) // 隣接キーとのすき間をあける
            .weight(1f) // 横幅を均等にする
            .fillMaxHeight(), // 高さを最大まで使う
          normalColor = Color.White, // 押していない時の背景色
          pressedColor = Color.LightGray,// 押した時の背景色
          onPlay = {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
          }
        ) // KeyBoxの閉じカッコ
      } // forEachの閉じカッコ
    } // 白鍵盤Rowの閉じカッコ
    // 黒鍵盤の配置（白鍵盤の上に重ねる）
    Row(modifier = Modifier.fillMaxSize()) {
      Spacer(modifier = Modifier.weight(0.5f)) // 最初に半分の余白
      blackKeys.forEach { soundId ->
        if (soundId == null) {
          Spacer(modifier = Modifier.weight(1f)) // 黒鍵盤がない場合
        } else {
          KeyBox( // 黒鍵盤1つ分
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight(0.55f) // 黒鍵盤は白鍵盤より高さを短くする
              .padding(15.dp, 1.dp, 15.dp, 0.dp),
            normalColor = Color.Black, // 押していない時の背景色
            pressedColor = Color.Gray, // 押した時の背景色
            onPlay = {
              soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }
          ) // KeyBoxの閉じカッコ
        }
      } // forEachの閉じカッコ
      Spacer(modifier = Modifier.weight(0.5f)) // 最後の半分の余白
    } // 黒鍵盤Rowの閉じカッコ
  } // Boxの閉じカッコ
} // Mainの閉じカッコ

@Composable
fun KeyBox(
  modifier: Modifier = Modifier,
  normalColor: Color, // タッチしていない時の背景色
  pressedColor: Color, // タッチした時の背景色
  onPlay: () -> Unit, // タッチした時の処理
) {
  var pressed by remember { mutableStateOf(false) } // タッチの状態変数
  Box(
    // 鍵盤1つ分
    modifier = modifier
      .border(1.dp, Color.Gray) // 枠線を描く
      .background(if (pressed) pressedColor else normalColor) // 背景色の切り替え
      .pointerInput(Unit) {
        detectTapGestures(
          onPress = { // タッチ時の処理
            pressed = true // タッチし始めた
            onPlay() // 音を鳴らすなどの処理を実行
            try {
              awaitRelease() // 離すまで待つ
            } finally {
              pressed = false // 離したら元の状態に戻す
            }
          }
        )
      } // pointerInputの閉じカッコ
  ) // Boxの閉じカッコ
} // KeyBoxの閉じカッコ

@Preview(showBackground = true)
@Composable
fun MainPreview() {
  PracticePianoAppTheme {
    Main()
  }
}
