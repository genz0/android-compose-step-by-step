package com.example.practicetimerapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Locale
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// タイマーの状態を表すenum
enum class TimerState {
  STOPPED, // 停止状態
  RUNNING, // 実行状態
  PAUSED, // 一時停止状態
}

// DataStoreを利用するための拡張関数
private val Context.dataStore by preferencesDataStore(name = "settings")

// DataStoreに保存する際のキー
private val totalTimeKey = longPreferencesKey("total_time")

class TimerViewModel : ViewModel() {

  // タイマーの初期値(デフォルト60秒)
  private var initTime = 60_000L

  // カウント時間
  private var totalTime by mutableLongStateOf(initTime)

  // 残り時間
  private var timeLeft by mutableLongStateOf(initTime)

  // タイマー用job
  private var timer: Job? = null

  // 現在の動作状態
  private var state by mutableStateOf(TimerState.STOPPED)

  // 実行中を判定する、カスタムゲッタ
  val isRunning get() = state == TimerState.RUNNING

  // +1分が可能か判定する、カスタムゲッタ。（残り時間が59分以下のときtrue）
  val canPlus1 get() = timeLeft <= 60_000L * 59

  // -1分が可能か判定する、カスタムゲッタ。（残り時間が1分より大きいときtrue）
  val canMinus1 get() = timeLeft > 60_000L

  // 進捗状況を表すカスタムゲッタ
  val progress get() = timeLeft / totalTime.toFloat()

  // 終了状態
  var finish by mutableStateOf(false)
    private set

  // 残り時間を導出するカスタムゲッタ
  val timeLeftText: String
    get() {
      val seconds = (timeLeft / 1000) % 60
      val minutes = (timeLeft / 1000) / 60
      return String.format(Locale.JAPANESE, "%02d:%02d", minutes, seconds)
    }

  // 合計時間を導出するカスタムゲッタ
  val totalTimeText: String
    get() {
      val seconds = (totalTime / 1000) % 60
      val minutes = (totalTime / 1000) / 60
      return String.format(Locale.JAPANESE, "%02d:%02d", minutes, seconds)
    }

  // +1分
  fun plus1() {
    timeLeft += 60_000L
    totalTime += 60_000L
  }

  // -1分
  fun minus1() {
    timeLeft -= 60_000L
    totalTime -= 60_000L
  }

  // カウントダウン or 一時停止を切り替える処理
  fun startOrPauseTimer() {
    when (state) {
      TimerState.STOPPED, TimerState.PAUSED -> { // 一時停止or停止の時はカウント開始
        countDown()
      }

      TimerState.RUNNING -> { // カウント中なら一時停止
        // 一時停止にする（countDownのループ終了条件になる）
        state = TimerState.PAUSED
      }
    }
  }

  // カウントダウン
  private fun countDown() {
    // 起動中のタイマーがあれば停止する
    timer?.cancel()
    // 実行中に変更
    state = TimerState.RUNNING
    timer = viewModelScope.launch {
      // 実行中且つ残り時間がある間ループ
      while (timeLeft > 0 && isRunning) {
        delay(100) //100ms待つ(スレッドをブロックしないサスペンド関数)
        timeLeft -= 100 // 100ms減らす
      }
      // カウントダウンが終わっていれば、終了状態に遷移
      if (timeLeft <= 0) {
        timeLeft = 0
        // 状態を停止中に変更
        state = TimerState.STOPPED
        // 完了フラグON
        finish = true
      }
    } // launchの閉じカッコ
  } // countDownの閉じカッコ

  // タイマーリセット
  fun resetTimer() {
    // 状態を停止に変更
    state = TimerState.STOPPED
    // 起動中のタイマーがあれば停止する
    timer?.cancel()
    timer = null

    // 各データを初期化する
    totalTime = initTime
    timeLeft = initTime
  }

  // カウントダウン終了からの復帰
  fun applyFinish() {
    timeLeft = totalTime
    finish = false
  }

  // DataStoreにカウント時間を保存し、初期値も更新する
  fun saveTotalTime(context: Context) {
    viewModelScope.launch {
      context.dataStore.edit { preferences ->
        preferences[totalTimeKey] = totalTime
        initTime = totalTime
      }
    }
  }

  // DataStoreから初期値を復元し、その後全体をリセットする
  fun loadTotalTime(context: Context) {
    viewModelScope.launch {
      val preferences = context.dataStore.data.first()
      val restored = preferences[totalTimeKey] ?: initTime // 値がないときは初期値を使う
      initTime = restored
      resetTimer()
    }
  }
} // TimerViewModelの閉じカッコ
