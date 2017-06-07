package com.mumarov.todo.todo.ui.todo_list_overview

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import com.mumarov.todo.todo.TodoApplication
import com.mumarov.todo.todo.database.TodoAppDatabase
import com.mumarov.todo.todo.database.entities.TodoItem
import com.mumarov.todo.todo.database.entities.TodoList
import javax.inject.Inject

class TodoListOverviewViewModel constructor(application: Application) : AndroidViewModel(application) {
  @Inject lateinit var db: TodoAppDatabase

  init {
    (application as TodoApplication).appComponent.inject(this)
  }


  fun getTodoLists() = db.todoListDao().getTodoLists()

  fun createTodoList(todoList: TodoList, afterCreate: (todoListId: Long) -> Unit) {
    Thread(Runnable {
      val todoListId = db.todoListDao().insertTodoList(todoList)
      afterCreate(todoListId)
    }).start()
  }

  fun getTodoListItems(todoListId: Long) = db.todoItemDao().getTodoItemsForTodoList(todoListId)

  fun createTodoListWithItems(todoList: TodoList, todoListItems: List<TodoItem>) {
    Thread(Runnable {
      db.beginTransaction()
      val insertedTodoListId = db.todoListDao().insertTodoList(todoList)
      Log.d("Created todo list id", insertedTodoListId.toString())
      todoListItems.forEach {
        it.todoListId = insertedTodoListId
      }
      val itemIds = db.todoItemDao().insertTodoItems(todoListItems)
      Log.d("Created todo item ids", itemIds.toString())

      db.endTransaction()
    }).start()
  }
}