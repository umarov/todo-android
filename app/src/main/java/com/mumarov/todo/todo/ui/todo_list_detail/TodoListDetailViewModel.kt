package com.mumarov.todo.todo.ui.todo_list_detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.mumarov.todo.todo.TodoApplication
import com.mumarov.todo.todo.database.TodoAppDatabase
import com.mumarov.todo.todo.database.entities.TodoItem
import javax.inject.Inject

class TodoListDetailViewModel constructor(application: Application) : AndroidViewModel(application) {
  @Inject lateinit var db: TodoAppDatabase

  init {
    (application as TodoApplication).appComponent.inject(this)
  }

  fun getTodoListItems(todoListId: Long) = db.todoItemDao().getTodoItemsForTodoList(todoListId)

  fun createTodoItem(todoItems: List<TodoItem>) = Thread(Runnable { db.todoItemDao().insertTodoItems(todoItems) }).start()

  fun updateTodoItem(todoItem: TodoItem) = Thread(Runnable { db.todoItemDao().updateTodoItem(todoItem) }).start()

  fun deleteTodoItem(todoItem: TodoItem) = Thread(Runnable { db.todoItemDao().deleteTodoItem(todoItem) }).start()
}