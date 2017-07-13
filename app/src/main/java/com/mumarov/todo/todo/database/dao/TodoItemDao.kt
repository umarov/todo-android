package com.mumarov.todo.todo.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.mumarov.todo.todo.database.entities.TodoItem

@Dao
interface TodoItemDao {
  @Query("SELECT * FROM todo_items WHERE todo_items.id = :id")
  fun getTodoItem(id: Long): LiveData<TodoItem>

  @Query("SELECT * FROM todo_items WHERE todo_items.todo_list_id = :todoListId")
  fun getTodoItemsForTodoList(todoListId: Long): LiveData<List<TodoItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTodoItems(todoItems: List<TodoItem>): LongArray

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateTodoItem(todoItem: TodoItem)

  @Delete
  fun deleteTodoItem(todoItem: TodoItem)

  @Delete
  fun deleteTodoItems(todoItems: List<TodoItem>)
}