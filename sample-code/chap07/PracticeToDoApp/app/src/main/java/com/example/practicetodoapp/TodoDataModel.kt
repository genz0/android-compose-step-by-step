package com.example.practicetodoapp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


// 1つのTo-Doを表すクラス
@Entity(tableName = "todos") // テーブル名を指定
data class Todo(
  @PrimaryKey(autoGenerate = true) // 主キー（自動採番）
  val id: Long = 0, // キー
  val title: String, // タイトル
  val memo: String, // メモ
  val isCompleted: Boolean = false, // 完了状態
  val position: Int = 0 // 表示順
) {
  val isNew: Boolean // 新規To-Doを表す、カスタムゲッタ
    get() = id == 0L
}

// データを操作するDao
@Dao
interface TodoDao {
  @Query("SELECT * FROM todos ORDER BY position")
  fun getAll(): Flow<List<Todo>>

  @Insert
  suspend fun insert(todo: Todo): Long

  @Update
  suspend fun update(vararg todos: Todo)

  @Query("SELECT MAX(position) FROM todos")
  suspend fun getMaxPosition(): Int?

  @Query("DELETE FROM todos WHERE isCompleted = TRUE")
  suspend fun deleteCompletedTodos()

}

// Roomの操作窓口となるクラス
@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
  abstract fun todoDao(): TodoDao

  companion object {
    private var instance: TodoDatabase? = null

    fun getInstance(context: Context): TodoDatabase {
      instance?.let { return it }
      val db = Room.databaseBuilder(
        context.applicationContext,
        TodoDatabase::class.java,
        "todo_database"
      ).build()
      instance = db
      return db
    }
  }
}
