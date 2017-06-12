package com.mumarov.todo.todo.ui.todo_list_detail

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mumarov.todo.todo.MainActivity
import com.mumarov.todo.todo.R
import com.mumarov.todo.todo.database.entities.TodoItem

class TodoListDetailFragment : LifecycleFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {

    val todoListOverviewFragment = inflater.inflate(R.layout.fragment_todo_list_detail, container, false)
    val todoListItemsRecyclerView = todoListOverviewFragment.findViewById(R.id.todo_list_items_recycler_view) as RecyclerView


    val todoListDetailViewModel = ViewModelProviders.of(this).get(TodoListDetailViewModel::class.java)
    val todoListId = arguments.getLong(MainActivity.TODO_LIST_ID)

    todoListDetailViewModel.getTodoListItems(todoListId).observe(this, Observer<List<TodoItem>> {
      val newTodoItem = TodoItem(0, "", "", false, todoListId)
      val todoItems = it?.toMutableList()
      todoItems?.add(newTodoItem)
      val todoListDetailAdapter = TodoListDetailAdapter(todoItems, context) { todoItem, actionType ->
        when (actionType) {
          TodoItem.CREATE -> todoListDetailViewModel.createTodoItem(listOf(todoItem))
          TodoItem.UPDATE -> todoListDetailViewModel.updateTodoItem(todoItem)
          TodoItem.DELETE -> todoListDetailViewModel.deleteTodoItem(todoItem)
        }
      }

      todoListItemsRecyclerView.adapter = todoListDetailAdapter
      val layoutManager = LinearLayoutManager(this.context)
      layoutManager.stackFromEnd = true
      todoListItemsRecyclerView.layoutManager = layoutManager
    })

    return todoListOverviewFragment
  }
}
