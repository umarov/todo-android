package com.mumarov.todo.todo.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.mumarov.todo.todo.database.TodoAppDatabase
import com.mumarov.todo.todo.database.dao.TodoItemDao
import com.mumarov.todo.todo.database.dao.TodoListDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
  @Singleton @Provides
  fun provideDb(context: Context): TodoAppDatabase {
    return TodoAppDatabase.createPersistentDatabase(context)
  }

  @Singleton @Provides
  fun provideTodoListDao(db: TodoAppDatabase): TodoListDao {
    return db.todoListDao()
  }

  @Singleton @Provides
  fun provideTodoItemDao(db: TodoAppDatabase): TodoItemDao {
    return db.todoItemDao()
  }
}