package com.mumarov.todo.todo.ui.todo_list_detail

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.mumarov.todo.todo.MainActivity
import com.mumarov.todo.todo.R
import com.mumarov.todo.todo.database.entities.TodoItem
import io.reactivex.disposables.Disposable

class TodoListDetailFragment : LifecycleFragment() {
  lateinit var todoItemUpdateDisposable: Disposable
  lateinit var todoItemDeleteDisposable: Disposable
  lateinit var todoItemCreateDisposable: Disposable

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {

    val todoListOverviewFragment = inflater.inflate(R.layout.fragment_todo_list_detail, container, false)
    val todoListItemsRecyclerView = todoListOverviewFragment.findViewById(R.id.todo_list_items_recycler_view) as RecyclerView
    val newTodoEnterTextLayout = todoListOverviewFragment.findViewById(R.id.new_todo_enter_text)
    val addNewTodoItemButton = todoListOverviewFragment.findViewById(R.id.todo_list_add_todo_item_button) as Button
    val saveNewTodoItemButton = todoListOverviewFragment.findViewById(R.id.new_todo_list_create_todo_item_button) as Button
    val newTodoItemTextInputLayout = todoListOverviewFragment.findViewById(R.id.new_todo_item_text_input_layout) as TextInputLayout


    val todoListDetailViewModel = ViewModelProviders.of(this).get(TodoListDetailViewModel::class.java)
    val todoListId = arguments.getLong(MainActivity.TODO_LIST_ID)

    todoListDetailViewModel.getTodoListItems(todoListId).observe(this, Observer<List<TodoItem>> {
      val newTodoItem = TodoItem(0, "", "", false, todoListId)
      val todoItems = it?.toMutableList()
      todoItems?.add(newTodoItem)
      val todoListDetailAdapter = TodoListDetailAdapter(todoItems, context)
      todoListItemsRecyclerView.adapter = todoListDetailAdapter
      todoListItemsRecyclerView.layoutManager = LinearLayoutManager(this.context)

      todoItemUpdateDisposable = todoListDetailAdapter.getTodoItemUpdated().subscribe {
        todoListDetailViewModel.updateTodoItem(it)
      }

      todoItemDeleteDisposable = todoListDetailAdapter.getTodoItemDeleted().subscribe {
        todoListDetailViewModel.deleteTodoItem(it)
      }

      todoItemCreateDisposable = todoListDetailAdapter.getTodoItemCreated().subscribe {
        todoListDetailViewModel.createTodoItem(listOf(it))
      }
    })

    Log.d(this.javaClass.name, todoListId.toString())

    addNewTodoItemButton.setOnClickListener {
      if (newTodoEnterTextLayout.visibility == View.VISIBLE) {
        newTodoEnterTextLayout.visibility = View.GONE
        addNewTodoItemButton.text = getString(R.string.todo_list_add_todo_item_button_text)
      } else {
        newTodoEnterTextLayout.visibility = View.VISIBLE
        addNewTodoItemButton.text = getString(R.string.todo_list_add_todo_item_cancel_button_text)
      }
    }

    saveNewTodoItemButton.setOnClickListener {
      newTodoItemTextInputLayout.editText?.let {
        val todoItem = TodoItem(0, it.text.toString(), "", false, todoListId)
        todoListDetailViewModel.createTodoItem(listOf(todoItem))
        newTodoEnterTextLayout.visibility = View.GONE
        addNewTodoItemButton.text = getString(R.string.todo_list_add_todo_item_button_text)

        it.setText("")
      }
    }

    return todoListOverviewFragment
  }

  override fun onDestroy() {
    super.onDestroy()

    todoItemUpdateDisposable.dispose()
    todoItemDeleteDisposable.dispose()
  }

  companion object {
    val TODO_LIST_ID = "todo_list_id"

    fun createInstance(todoListId: Long): TodoListDetailFragment {
      val todoListDetailFragment = TodoListDetailFragment()
      val args = Bundle()
      args.putLong(TODO_LIST_ID, todoListId)

      todoListDetailFragment.arguments = args

      return todoListDetailFragment
    }
  }



}
