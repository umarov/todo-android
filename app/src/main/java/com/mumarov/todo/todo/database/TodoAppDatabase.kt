package com.mumarov.todo.todo.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.mumarov.todo.todo.database.dao.TodoItemDao
import com.mumarov.todo.todo.database.dao.TodoListDao
import com.mumarov.todo.todo.database.entities.TodoItem
import com.mumarov.todo.todo.database.entities.TodoList

@Database(version = 1, entities = arrayOf(TodoItem::class, TodoList::class))
abstract class TodoAppDatabase: RoomDatabase() {
  abstract fun todoItemDao(): TodoItemDao
  abstract fun todoListDao(): TodoListDao

  companion object {
    private const val DB_NAME = "todoapp.db"

    fun createInMemoryDatabase(context: Context): TodoAppDatabase
            = Room.inMemoryDatabaseBuilder(context.applicationContext, TodoAppDatabase::class.java).build()

    fun createPersistentDatabase(context: Context): TodoAppDatabase
            = Room.databaseBuilder(context.applicationContext, TodoAppDatabase::class.java, DB_NAME).build()
  }
}