package com.mumarov.todo.todo.database.entities

import android.arch.persistence.room.*


@Entity(tableName="todo_items",
        indices = arrayOf(Index("completed"), Index("todo_list_id")),
        foreignKeys = arrayOf(
          ForeignKey(
            entity = TodoList::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("todo_list_id")
          )
        )
)
data class TodoItem(@PrimaryKey(autoGenerate = true)
                    var id: Long,
                    var title: String,
                    var description: String,
                    var completed: Boolean,
                    @ColumnInfo(name = "todo_list_id")
                    var todoListId: Long) {

  companion object {
    const val CREATE = 0
    const val UPDATE = 1
    const val DELETE = 2
  }
}


