package com.mumarov.todo.todo.ui.todo_list_overview

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Handler
import android.os.Looper
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

  inline fun createTodoList(todoList: TodoList, crossinline afterCreate: (todoListId: Long) -> Unit) {
    Thread(Runnable {
      val todoListId = db.todoListDao().insertTodoList(todoList)

      Handler(Looper.getMainLooper()).post { afterCreate(todoListId) }
    }).start()
  }

  inline fun deleteTodoList(todoList: TodoList, crossinline afterDelete: () -> Unit) {
    Thread(Runnable {
      todoList.listItems.forEach {
        db.todoItemDao().deleteTodoItem(it)
      }

      db.todoListDao().deleteTodoList(todoList)

      Handler(Looper.getMainLooper()).post { afterDelete() }
    }).start()
  }

  fun getTodoListItems(todoListId: Long) = db.todoItemDao().getTodoItemsForTodoList(todoListId)
}