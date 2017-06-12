package com.mumarov.todo.todo.ui.todo_list_overview


import android.app.Activity
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.mumarov.todo.todo.MainActivity

import com.mumarov.todo.todo.R
import com.mumarov.todo.todo.database.entities.TodoItem
import com.mumarov.todo.todo.database.entities.TodoList
import com.mumarov.todo.todo.util.closeKeyboard


class TodoListOverviewFragment : LifecycleFragment() {
  interface TodoListListener {
    fun OnTodoListClicked(todoList: TodoList, cardView: CardView)
    fun OnTodoListCreated(todoList: TodoList)
  }

  lateinit var todoListListener: TodoListListener
  lateinit var fragmentView: View
  lateinit var addTodoButton: Button
  lateinit var createTodoButton: Button
  lateinit var todoListsRecyclerView: RecyclerView
  lateinit var newTodoListTextInputLayout: TextInputLayout
  lateinit var newTodoBottomSheet: View

  val todoListOverviewAdapter: TodoListOverviewAdapter by lazy {
    TodoListOverviewAdapter(mutableListOf(), context, onTodoListDeleted, onTodoListClicked)
  }

  val todoListOverViewViewModel: TodoListOverviewViewModel by lazy {
    ViewModelProviders.of(this).get(TodoListOverviewViewModel::class.java)
  }

  val onTodoListDeleted = { todoList: TodoList ->
    todoListOverViewViewModel.deleteTodoList(todoList)
  }

  val onTodoListClicked = { todoList: TodoList, index: Int ->
    val cardView = todoListsRecyclerView.getChildAt(index) as CardView
    todoListListener.OnTodoListClicked(todoList, cardView)
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
    fragmentView = inflater.inflate(R.layout.fragment_todo_list_overview, container, false)

    addTodoButton = fragmentView.findViewById(R.id.todo_list_add_todo_button) as Button
    createTodoButton = fragmentView.findViewById(R.id.new_todo_list_create_list_button) as Button
    todoListsRecyclerView = fragmentView.findViewById(R.id.todo_lists_recycler_view) as RecyclerView
    newTodoListTextInputLayout = fragmentView.findViewById(R.id.new_todo_list_text_input_layout) as TextInputLayout
    newTodoBottomSheet = fragmentView.findViewById(R.id.new_todo_list_bottom_sheet)

    val newTodoBottomSheetBehavior = BottomSheetBehavior.from(newTodoBottomSheet)

    todoListsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

    newTodoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    newTodoBottomSheetBehavior.peekHeight = 0

    todoListsRecyclerView.adapter = todoListOverviewAdapter

    todoListOverViewViewModel.getTodoLists().observe(this, Observer<List<TodoList>> { todoLists ->

      todoListOverviewAdapter.todoLists = todoLists as MutableList<TodoList>
      todoListOverviewAdapter.notifyItemRangeChanged(0, todoLists.size)

      todoListOverviewAdapter.todoLists.forEach { todoList ->
        todoListOverViewViewModel.getTodoListItems(todoList.id)
                .observe(this, Observer<List<TodoItem>> { todoItems: List<TodoItem>? ->
                  val items = todoItems as List<TodoItem>

                  todoList.listItems = items

                  if (todoListOverviewAdapter.todoLists.indexOf(todoList) >= 0) {
                    todoListOverviewAdapter.notifyItemChanged(todoListOverviewAdapter.todoLists.indexOf(todoList))
                  }
                })
      }
    })


    addTodoButton.setOnClickListener {
      newTodoBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
      newTodoListTextInputLayout.editText?.requestFocus()
    }

    createTodoButton.setOnClickListener {
      newTodoListTextInputLayout.editText?.let {
        if (it.text.isNotEmpty()) {
          context.closeKeyboard(it.windowToken)
          newTodoBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

          todoListOverViewViewModel.createTodoList(it.text.toString(), {
            todoListListener.OnTodoListCreated(it)
          })

          it.setText("")
        }
      }
    }


    return fragmentView
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
