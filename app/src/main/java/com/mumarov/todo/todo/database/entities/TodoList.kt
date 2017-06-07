package com.mumarov.todo.todo.database.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "todo_lists")
data class TodoList(@PrimaryKey(autoGenerate = true)
                    var id: Long,
                    var name: String) {

  @Ignore
  var listItems: List<TodoItem> = emptyList()
}