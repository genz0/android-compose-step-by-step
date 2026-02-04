package com.example.practicetodoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TodoViewModel(application: Application) : AndroidViewModel(application) {
  // 完了済みTo-Doの表示状態
  private val _showCompleted = MutableStateFlow(false)
  val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()

  // 完了済みTo-Doの表示状態を切り替える
  fun setShowCompleted(show: Boolean) {
    _showCompleted.value = show
  }

  // DBの初回の読み込み中かを管理するフラグ
  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  // データ操作クラスよりDaoを取得
  private val todoDao = TodoDatabase.getInstance(application.applicationContext).todoDao()

  // DBから取得したTo-Doリスト
  private val _todos = todoDao.getAll()
    .onEach { _isLoading.value = false } // データ更新のたびに呼び出される
    .catch { _isLoading.value = false; throw it } // 例外発生時に呼び出される

  // UIに公開するフィルタリング済みのTo-Doリスト
  val showTodos: StateFlow<List<Todo>> =
    combine(_todos, _showCompleted) { todos, showCompleted ->
      if (showCompleted) todos else todos.filter { !it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  //  To-Doの完了/未完了切り換え
  fun toggleComplete(todo: Todo) {
    // isCompletedを反転して更新する
    val updated = todo.copy(isCompleted = !todo.isCompleted)
    saveTodo(updated)
  } // toggleCompleteの閉じカッコ

  // To-Doを保存する関数
  fun saveTodo(todo: Todo) {
    viewModelScope.launch {
      if (todo.isNew) { // 新規ならinsert、既存ならupdate
        // positionは最大値+1で設定
        val maxPos = todoDao.getMaxPosition() ?: 0
        val newTodo = todo.copy(position = maxPos + 1)
        todoDao.insert(newTodo)
      } else {
        todoDao.update(todo)
      }
    } // viewModelScope.launchの閉じカッコ
  } // saveTodoの閉じカッコ

  // 完了済みTo-Doの削除
  fun deleteCompletedTodos() {
    viewModelScope.launch {
      todoDao.deleteCompletedTodos()
    }
  } // deleteCompletedTodosの閉じカッコ

  // To-Do順の入れ替え(↑)
  fun moveUp(todo: Todo) {
    viewModelScope.launch {
      val current = showTodos.value
      val idx = current.indexOfFirst { it.id == todo.id }
      val prev = current[idx - 1]
      // positionを入れ替えて更新
      todoDao.update(
        todo.copy(position = prev.position),
        prev.copy(position = todo.position),
      )
    }
  } // moveUpの閉じカッコ

  // To-Do順の入れ替え(↓)
  fun moveDown(todo: Todo) {
    viewModelScope.launch {
      val current = showTodos.value
      val idx = current.indexOfFirst { it.id == todo.id }
      val next = current[idx + 1]
      // positionを入れ替えて更新
      todoDao.update(
        todo.copy(position = next.position),
        next.copy(position = todo.position),
      )
    }
  } // moveDownの閉じカッコ
} // TodoViewModelの閉じカッコ
