package com.mumarov.todo.todo.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.mumarov.todo.todo.database.entities.TodoItem
import com.mumarov.todo.todo.database.entities.TodoList

@Dao
interface TodoListDao {
  @Query("SELECT * FROM todo_lists")
  fun getTodoLists(): LiveData<List<TodoList>>

  @Query("SELECT * FROM todo_items WHERE todo_items.todo_list_id = :p0")
  fun getTodoItems(id: Long): LiveData<List<TodoItem>>

  @Query("SELECT * FROM todo_lists WHERE todo_lists.id = :p0")
  fun getTodoList(id: Long): LiveData<TodoList>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTodoList(todoList: TodoList): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun updateTodoList(todoList: TodoList)

  @Delete
  fun deleteTodoList(todoList: TodoList)
}