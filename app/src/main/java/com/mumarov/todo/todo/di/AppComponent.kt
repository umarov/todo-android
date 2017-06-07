package com.mumarov.todo.todo.di


import com.mumarov.todo.todo.ui.todo_list_detail.TodoListDetailViewModel
import dagger.Component
import javax.inject.Singleton
import com.mumarov.todo.todo.ui.todo_list_overview.TodoListOverviewViewModel

@Singleton
@Component(modules = arrayOf(
    AndroidModule::class,
    AppModule::class
  )
)
interface AppComponent {
  fun inject(todoListOverviewViewModel: TodoListOverviewViewModel)
  fun inject(todoListDetailViewModel: TodoListDetailViewModel)
}