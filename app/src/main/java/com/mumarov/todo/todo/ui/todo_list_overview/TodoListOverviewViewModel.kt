package com.mumarov.todo.todo.ui.todo_list_overview

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Handler
import android.os.Looper
import com.mumarov.todo.todo.TodoApplication
import com.mumarov.todo.todo.database.TodoAppDatabase
import com.mumarov.todo.todo.database.entities.TodoList
import javax.inject.Inject

class TodoListOverviewViewModel constructor(application: Application) : AndroidViewModel(application) {
  @Inject lateinit var db: TodoAppDatabase

  init {
    (application as TodoApplication).appComponent.inject(this)
  }

  fun getTodoLists() = db.todoListDao().getTodoLists()

  inline fun createTodoList(name: String, crossinline afterCreate: (todoList: TodoList) -> Unit) {
    Thread(Runnable {
      val todoList = TodoList(0, name)
      val todoListId = db.todoListDao().insertTodoList(todoList)
      todoList.id = todoListId

      Handler(Looper.getMainLooper()).post { afterCreate(todoList) }
    }).start()
  }

  inline fun deleteTodoList(todoList: TodoList, crossinline afterDelete: () -> Unit) {
    Thread(Runnable {
      db.todoItemDao().deleteTodoItems(todoList.listItems)
      db.todoListDao().deleteTodoList(todoList)
      Handler(Looper.getMainLooper()).post { afterDelete() }
    }).start()
  }

  fun getTodoListItems(todoListId: Long) = db.todoItemDao().getTodoItemsForTodoList(todoListId)
}