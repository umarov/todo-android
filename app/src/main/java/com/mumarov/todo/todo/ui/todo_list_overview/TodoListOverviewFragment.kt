package com.mumarov.todo.todo.ui.todo_list_overview


import android.app.Activity
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.Toolbar
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.mumarov.todo.todo.MainActivity

import com.mumarov.todo.todo.R
import com.mumarov.todo.todo.database.entities.TodoItem
import com.mumarov.todo.todo.database.entities.TodoList
import com.mumarov.todo.todo.util.closeKeyboard
import com.mumarov.todo.todo.util.openKeyboard


class TodoListOverviewFragment : LifecycleFragment() {
  interface TodoListListener {
    fun OnTodoListClicked(todoList: TodoList, cardView: CardView, toolbar: Toolbar)
    fun OnTodoListCreated(todoList: TodoList)
  }

  lateinit var todoListListener: TodoListListener

  val todoListOverviewAdapter: TodoListOverviewAdapter by lazy {
    TodoListOverviewAdapter(emptyList(), context, {
      todoListOverViewViewModel.deleteTodoList(it) {
        todoListOverviewAdapter.notifyDataSetChanged()
      }
    }) { todoList, cardView, toolbar ->
      todoListListener.OnTodoListClicked(todoList, cardView, toolbar)
    }
  }

  val todoListOverViewViewModel: TodoListOverviewViewModel by lazy {
    ViewModelProviders.of(this).get(TodoListOverviewViewModel::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
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

    todoListsRecyclerView.adapter = todoListOverviewAdapter

    if (todoListOverviewAdapter.todoLists.isEmpty()) {
      todoListOverViewViewModel.getTodoLists().observe(this, Observer<List<TodoList>> { todoLists ->
        todoListOverviewAdapter.todoLists = todoLists as List<TodoList>

        todoLists.forEach { todoList ->
          todoListOverViewViewModel.getTodoListItems(todoList.id).observe(this, Observer<List<TodoItem>> {
            todoList.listItems = it as List<TodoItem>
            todoListOverviewAdapter.notifyItemChanged(todoLists.indexOf(todoList))
          })
        }
      })
    }

    addTodoButton.setOnClickListener {
      newTodoBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
      newTodoListTextInputLayout.editText?.requestFocus()
    }

    createTodoButton.setOnClickListener {
      newTodoListTextInputLayout.editText?.let {
        if (it.text.isNotEmpty()) {
          context.closeKeyboard(it.windowToken)
          newTodoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

          val todoList = TodoList(0, it.text.toString())

          todoListOverViewViewModel.createTodoList(todoList, {
            todoList.id = it
            todoListListener.OnTodoListCreated(todoList)
          })

          it.setText("")
        }
      }

    }

    return todoListOverviewFragment
  }

  override fun onAttach(activity: Activity?) {
    super.onAttach(activity)

    try {
      todoListListener = (activity as MainActivity)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
