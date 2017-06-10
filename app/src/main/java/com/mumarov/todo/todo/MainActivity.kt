package com.mumarov.todo.todo

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import com.mumarov.todo.todo.database.entities.TodoList
import com.mumarov.todo.todo.ui.todo_list_detail.TodoListDetailFragment
import com.mumarov.todo.todo.ui.todo_list_overview.TodoListOverviewFragment

class MainActivity : AppCompatActivity(), TodoListOverviewFragment.TodoListListener {
  val myToolbar: Toolbar by lazy { findViewById(R.id.my_toolbar) as Toolbar }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    setSupportActionBar(myToolbar)

    supportFragmentManager
            .beginTransaction()
            .add(R.id.main_activity_content, TodoListOverviewFragment(), "Todo List Overview")
            .commit()
  }

  override fun OnTodoListClicked(todoList: TodoList, cardView: CardView) {
    goToDetailFragment(todoList, cardView)
  }

  override fun OnTodoListCreated(todoList: TodoList) {
    goToDetailFragment(todoList, null)
  }

  private fun goToDetailFragment(todoList: TodoList,
                                 cardView: CardView? = null) {
    val todoListDetailFragment = TodoListDetailFragment()
    val args = Bundle()
    args.putLong(TODO_LIST_ID, todoList.id)
    todoListDetailFragment.arguments = args

    var transaction = supportFragmentManager
            .beginTransaction()
            .addToBackStack("Todo List Detail")

    cardView?.let {
      transaction = transaction.addSharedElement(cardView, ViewCompat.getTransitionName(cardView))
    }

    transaction
            .replace(R.id.main_activity_content, todoListDetailFragment)
            .commit()

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    myToolbar.title = todoList.name
    myToolbar.setNavigationOnClickListener {
      setNavBackToNormal()
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()

    setNavBackToNormal()
  }

  private fun setNavBackToNormal() {
    supportFragmentManager.popBackStack()
    myToolbar.title = "Todo"
    supportActionBar?.setDisplayHomeAsUpEnabled(false)
    supportActionBar?.setDisplayShowHomeEnabled(false)
  }
  companion object {
    val TODO_LIST_ID = "todo_list_id"
  }

}
