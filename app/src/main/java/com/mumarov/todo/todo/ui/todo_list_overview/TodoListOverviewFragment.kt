package com.mumarov.todo.todo.ui.todo_list_overview


import android.app.Activity
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.mumarov.todo.todo.MainActivity

import com.mumarov.todo.todo.R
import com.mumarov.todo.todo.database.entities.TodoItem
import com.mumarov.todo.todo.database.entities.TodoList
import io.reactivex.disposables.Disposable


class TodoListOverviewFragment : LifecycleFragment() {
  interface TodoListListener {
    fun OnTodoListClicked(todoList: TodoList)
    fun OnTodoListCreated(todoList: TodoList)
  }

  lateinit var todoListListener: TodoListListener
  lateinit var todoListClickedDisposable: Disposable

  val todoListOverViewViewModel: TodoListOverviewViewModel by lazy {
    ViewModelProviders.of(this).get(TodoListOverviewViewModel::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
    val context = this.context
    val todoListOverviewFragment = inflater.inflate(R.layout.fragment_todo_list_overview, container, false)

    val addTodoButton = todoListOverviewFragment.findViewById(R.id.todo_list_add_todo_button) as Button
    val createTodoButton = todoListOverviewFragment.findViewById(R.id.new_todo_list_create_list_button) as Button
    val todoListsRecyclerView = todoListOverviewFragment.findViewById(R.id.todo_lists_recycler_view) as RecyclerView
    val newTodoListTextInputLayout = todoListOverviewFragment.findViewById(R.id.new_todo_list_text_input_layout) as TextInputLayout
    val newTodoBottomSheet = todoListOverviewFragment.findViewById(R.id.new_todo_list_bottom_sheet)

    val newTodoBottomSheetBehavior = BottomSheetBehavior.from(newTodoBottomSheet)

    todoListsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

    newTodoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    newTodoBottomSheetBehavior.peekHeight = 0

    todoListOverViewViewModel.getTodoLists().observe(this, Observer<List<TodoList>> { todoLists ->
      val todoListOverviewAdapter = TodoListOverviewAdapter(todoLists, context)
      todoLists?.forEach { todoList ->
        todoListOverViewViewModel.getTodoListItems(todoList.id).observe(this, Observer<List<TodoItem>> {
          todoList.listItems = it as List<TodoItem>
          todoListOverviewAdapter.notifyItemChanged(todoLists.indexOf(todoList))
        })
      }

      todoListClickedDisposable = todoListOverviewAdapter.getClickEvent().subscribe({
        todoListListener.OnTodoListClicked(it)
      })
      todoListsRecyclerView.adapter = todoListOverviewAdapter

    })

    addTodoButton.setOnClickListener {
      newTodoBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    createTodoButton.setOnClickListener {
      newTodoListTextInputLayout.editText?.let {
        if (it.text.isNotEmpty()) {
          val todoList = TodoList(0, it.text.toString())

          todoListOverViewViewModel.createTodoList(todoList, {
            todoListListener.OnTodoListCreated(todoList)
          })

          it.setText("")
        }
      }

    }

    return todoListOverviewFragment
  }

  override fun onDestroy() {
    super.onDestroy()

    todoListClickedDisposable.dispose()
  }

  override fun onAttach(activity: Activity?) {
    super.onAttach(activity)

    try {
      todoListListener = (activity as MainActivity)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun onResume() {
    super.onResume()
  }
}
