package com.example.practicetimerapp

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicetimerapp.ui.theme.PracticeTimerAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PracticeTimerAppTheme {
        Main()
      }
    }
  }
}

@Composable
fun Main() {
  // viewModelを取得
  val viewModel: TimerViewModel = viewModel()

  Scaffold(
    bottomBar = { BottomView(viewModel) } // フッター部
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp),
      contentAlignment = Alignment.TopCenter
    ) {
      TimerView(viewModel) // タイマー部
    }
  }
  // メッセージ通知処理
  if (viewModel.finish) {
    FinishDialog(viewModel)
  }

} // Mainの閉じカッコ

// 完了時に表示するダイアログ
@Composable
fun FinishDialog(
  viewModel: TimerViewModel
) {
  val context = LocalContext.current

  DisposableEffect(Unit) {
    // 通知音のURIを取得
    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val mediaPlayer = MediaPlayer.create(context, uri).apply {
      isLooping = true // ループ再生
      start() // 再生開始
    }

    onDispose {
      // ダイアログを閉じたら停止＆解放
      mediaPlayer.stop()
      mediaPlayer.release()
    }
  }

  // シンプルなメッセージを表示するダイアログ
  AlertDialog(
    title = { Text("タイマー終了") },
    text = { Text("時間が来ました！") },
    onDismissRequest = { viewModel.applyFinish() },
    confirmButton = {
      TextButton(onClick = { viewModel.applyFinish() }) {
        Text("OK")
      }
    })
} // FinishDialogの閉じカッコ

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TimerView(viewModel: TimerViewModel) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      val size = minOf(maxWidth, maxHeight)

      Box(
        modifier = Modifier
          .size(size)
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator( // 進捗グラフ
          progress = { viewModel.progress },
          modifier = Modifier.fillMaxSize(),
          strokeWidth = 16.dp
        )
        Text( // 中央のテキスト
          text = viewModel.timeLeftText,
          fontSize = 48.sp
        )
      }
    }

    OutlinedTextField( // タイマーの時間
      value = viewModel.totalTimeText,
      onValueChange = { },
      label = { Text("指定の時間") },
      readOnly = true,
      textStyle = LocalTextStyle.current.copy(
        fontSize = 24.sp,
        textAlign = TextAlign.Center
      )
    )
  }
} // TimerViewの閉じカッコ

// 画面下部のボタンリスト
@Composable
fun BottomView(viewModel: TimerViewModel) {
  val context = LocalContext.current
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min)
      .navigationBarsPadding()
      .padding(16.dp),
    horizontalArrangement = Arrangement.Center
  ) {
    FilledIconButton( // +1分ボタン
      onClick = { viewModel.plus1() },
      enabled = viewModel.canPlus1
    ) {
      Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.img_plus_1),
        contentDescription = "+1"
      )
    }
    FilledIconButton( // -1分ボタン
      onClick = { viewModel.minus1() },
      enabled = viewModel.canMinus1
    ) {
      Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.img_minus_1),
        contentDescription = "-1"
      )
    }
    VerticalDivider(
      modifier = Modifier
        .fillMaxHeight()
        .padding(4.dp),
      thickness = 2.dp,
    )
    FilledIconButton(
      // スタート・ポーズ
      onClick = { viewModel.startOrPauseTimer() },
    ) {
      val iconId = if (viewModel.isRunning) R.drawable.img_pause else R.drawable.img_play
      Icon(
        imageVector = ImageVector.vectorResource(id = iconId),
        contentDescription = "start/pause"
      )
    }
    FilledIconButton(
      // リセット
      onClick = { viewModel.resetTimer() },
    ) {
      Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.img_reset),
        contentDescription = "reset"
      )
    }
    VerticalDivider(
      modifier = Modifier
        .fillMaxHeight()
        .padding(4.dp),
      thickness = 2.dp,
    )
    FilledIconButton(
      // 設定保存
      onClick = {
        viewModel.saveTotalTime(context)
        Toast.makeText(context, "設定を保存しました", Toast.LENGTH_LONG).show() // ── ❷
      },
    ) {
      Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.img_save),
        contentDescription = "save"
      )
    }
    FilledIconButton(
      // 設定復元
      onClick = {
        viewModel.loadTotalTime(context)
        Toast.makeText(context, "設定を復元しました", Toast.LENGTH_LONG).show() // ── ❷
      },
    ) {
      Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.img_load),
        contentDescription = "load"
      )
    }
  } // Rowの閉じカッコ
} // BottomViewの閉じカッコ

@Preview(showBackground = true)
@Composable
fun MainPreview() {
  PracticeTimerAppTheme {
    Main()

  }
}
