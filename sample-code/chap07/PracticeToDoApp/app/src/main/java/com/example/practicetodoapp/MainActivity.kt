package com.example.practicetodoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practicetodoapp.ui.theme.PracticeToDoAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PracticeToDoAppTheme {
        Main()
      }
    }
  }
}

@Composable
fun Main() {
  // ViewModelの取得
  val viewModel: TodoViewModel = viewModel()
  // ViewModelのStateFlowをcollectAsStateWithLifecycleでComposeのStateに変換する
  val showTodos by viewModel.showTodos.collectAsStateWithLifecycle() // To-Doリスト
  val showCompleted by viewModel.showCompleted.collectAsStateWithLifecycle() //完了の表示
  val isLoading by viewModel.isLoading.collectAsStateWithLifecycle() // 読み込み中
  // 編集中のTo-Do
  var editingTodo by remember { mutableStateOf<Todo?>(null) }
  // 完了済みTo-Doの一括削除確認
  var showCleanupDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TodoTopBar(
        showCompleted = showCompleted,
        onToggleShowCompleted = { viewModel.setShowCompleted(it) },
        onDeleteCompleted = { showCleanupDialog = true },
      )
    },
    floatingActionButton = {
      TodoActionButton {
        // 新規To-Doの編集を開始
        editingTodo = Todo(title = "", memo = "")
      }
    }

  ) { innerPadding ->
    Box(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()
    ) {
      when { // 状態によってメインのViewを切り換える
        isLoading -> LoadingView()
        showTodos.isEmpty() -> EmptyView(message = "ToDoは0件です。\n次の予定ができたら＋で追加しましょう。")
        else -> TodoList(
          todos = showTodos,
          onMoveUp = { viewModel.moveUp(it) },
          onMoveDown = { viewModel.moveDown(it) },
          onToggleComplete = { viewModel.toggleComplete(it) },
          onEdit = { editingTodo = it }
        )
      }
    }
  }

  // editingTodoがnullではない場合にダイアログを表示
  editingTodo?.let { todo ->
    EditTodoDialog(
      todo = todo,
      onDismiss = { editingTodo = null },
    ) { savedTodo ->
      viewModel.saveTodo(savedTodo) // データ更新
      editingTodo = null
    }
  }
  // 完了したTo-Doを削除する確認ダイアログを表示
  if (showCleanupDialog) {
    AlertDialog(
      onDismissRequest = { showCleanupDialog = false },
      title = { Text("完了したToDoの削除") },
      text = { Text("完了したToDoをすべて削除します。よろしいですか？") },
      confirmButton = {
        TextButton(onClick = {
          viewModel.deleteCompletedTodos()
          showCleanupDialog = false
        }) { Text("削除") }
      },
      dismissButton = {
        TextButton(onClick = { showCleanupDialog = false }) {
          Text("キャンセル")
        }
      }
    )
  }
} // Mainの閉じカッコ

// ローディング中表示
@Composable
fun LoadingView(
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator(color = Color.Gray)
  }
} // LoadingViewの閉じカッコ


// 表示するTo-Doリストが空の場合に表示するUI部品
@Composable
fun EmptyView(
  message: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = message,
      color = Color.Gray
    )
  }
} // EmptyViewの閉じカッコ


// To-Doを追加・変更するダイアログ
@Composable
fun EditTodoDialog(
  todo: Todo,
  onDismiss: () -> Unit,
  onSaveTodo: (Todo) -> Unit
) {

  var updatedTitle by remember { mutableStateOf(todo.title) } // 編集中のタイトルを保持
  var updatedMemo by remember { mutableStateOf(todo.memo) } // 編集中のメモを保持

  Dialog(onDismissRequest = { onDismiss() }) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp)
          .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp) // 各項目の間隔
      ) {
        Text("ToDoの編集") // ダイアログのタイトル

        OutlinedTextField(
          value = updatedTitle,
          onValueChange = { updatedTitle = it },
          placeholder = { Text("タイトル") },
          label = { Text("ToDo") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = updatedMemo,
          onValueChange = { updatedMemo = it },
          label = { Text("メモ") },
          maxLines = 15, // 最大行数
          placeholder = { Text("メモ") },
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          horizontalArrangement = Arrangement.End, // ボタンを右端に配置
          modifier = Modifier.fillMaxWidth()
        ) {
          TextButton(onClick = { onDismiss() }) {
            Text("キャンセル")
          }
          Spacer(modifier = Modifier.width(8.dp))
          TextButton( // 保存ボタン
            enabled = updatedTitle.isNotEmpty(),
            onClick = {// To-Doを更新
              onSaveTodo(todo.copy(title = updatedTitle, memo = updatedMemo))
            }
          ) {
            Text("保存")
          }
        }// Rowの閉じカッコ(ボタンのレイアウト)
      }// Columnの閉じカッコ
    }// Cardの閉じカッコ
  }// Dialogの閉じカッコ
} // EditTodoDialogの閉じカッコ

// To-Doを一覧表示するUI部品
@Composable
fun TodoList(
  todos: List<Todo>,
  onMoveUp: (Todo) -> Unit,
  onMoveDown: (Todo) -> Unit,
  onToggleComplete: (Todo) -> Unit,
  onEdit: (Todo) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(modifier = modifier) {
    itemsIndexed(
      items = todos,
      key = { _, todo -> todo.id }
    ) { index, todo ->
      val isFirst = index == 0
      val isLast = index == todos.lastIndex
      TodoCard(
        todo = todo,
        isFirst = isFirst,
        isLast = isLast,
        onMoveUp = onMoveUp,
        onMoveDown = onMoveDown,
        onToggleComplete = onToggleComplete,
        onEdit = onEdit
      )
    }
  }
} // TodoListの閉じカッコ

// 1件分のTo-Doを表示するUI部品
@Composable
fun TodoCard(
  todo: Todo,
  isFirst: Boolean,
  isLast: Boolean,
  onMoveUp: (Todo) -> Unit,
  onMoveDown: (Todo) -> Unit,
  onToggleComplete: (Todo) -> Unit,
  onEdit: (Todo) -> Unit,
) {
  Card( // 1件分のTo-Do
    modifier = Modifier
      .padding(2.dp) // カード間の余白を設定
      .fillMaxWidth() // 横幅いっぱいに広げる
      .clickable { onEdit(todo) } // カードタップのイベント処理
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically, // 垂直方向は中央に揃える
      horizontalArrangement = Arrangement.SpaceBetween, // 水平方向は左右に分ける
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Checkbox( // 左端のチェックボックス
        checked = todo.isCompleted,
        onCheckedChange = { onToggleComplete(todo) },
        modifier = Modifier.padding(8.dp) // 押せる領域を広げる
      )
      // To-Doの内容（タイトル＋メモ）
      Column(
        modifier = Modifier.weight(1f) // 横に広がって残りのスペースを使う
      ) {
        Text(text = todo.title, fontSize = 20.sp) // タイトル
        Text( // メモ
          text = todo.memo,
          fontSize = 12.sp,
          maxLines = 3, // 3行まで表示
          overflow = TextOverflow.Ellipsis // テキストが長い場合、3点リーダ(…)表示
        )
      } // Columnの閉じカッコ　To-Doの内容（テキスト＋メモ）

      // 上下ボタン（順番変更用）
      Column(
        verticalArrangement = Arrangement.Center, // 縦の中央に詰める
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        IconButton(
          onClick = { onMoveUp(todo) },
          enabled = !isFirst, // 先頭なら無効化
          modifier = Modifier.size(32.dp)
        ) {
          Icon(Icons.Default.KeyboardArrowUp, contentDescription = "上へ")
        }

        IconButton(
          onClick = { onMoveDown(todo) },
          enabled = !isLast, // 末尾なら無効化
          modifier = Modifier.size(32.dp)
        ) {
          Icon(Icons.Default.KeyboardArrowDown, contentDescription = "下へ")
        }
      } // 順番入れ替えボタン Columnの閉じカッコ
    } // Rowの閉じカッコ
  } // Cardの閉じカッコ
} // TodoCardの閉じカッコ

@Composable
fun TodoActionButton(onNewTodo: () -> Unit) {
  FloatingActionButton(onClick = onNewTodo) {
    Icon(Icons.Filled.Add, "add todo")
  }
} // TodoActionButtonの閉じカッコ

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTopBar(
  modifier: Modifier = Modifier,
  showCompleted: Boolean,
  onToggleShowCompleted: (Boolean) -> Unit,
  onDeleteCompleted: () -> Unit,
) {
  TopAppBar(
    modifier = modifier,
    title = { Text("ToDo") },
    actions = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("完了を表示", fontSize = 14.sp)
        Switch(
          checked = showCompleted,
          onCheckedChange = onToggleShowCompleted
        )
        IconButton(
          onClick = onDeleteCompleted
        ) {
          Icon(Icons.Filled.Delete, contentDescription = "完了したToDoを削除")
        }
      }
    }
  )
} // TodoTopBarの閉じカッコ

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  Main()
}