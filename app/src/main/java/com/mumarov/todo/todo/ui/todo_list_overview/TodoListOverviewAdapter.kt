package com.mumarov.todo.todo.ui.todo_list_overview

import android.content.Context
import android.graphics.Paint
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mumarov.todo.todo.R
import com.mumarov.todo.todo.database.entities.TodoItem
import com.mumarov.todo.todo.database.entities.TodoList

class TodoListOverviewAdapter(
        var todoLists: MutableList<TodoList>,
        val context: Context,
        inline val onTodoDeleted: (todoList: TodoList) -> Unit,
        inline val onTodoClicked: (
                todoList: TodoList,
                index: Int
        ) -> Unit): RecyclerView.Adapter<TodoListOverviewAdapter.ViewHolder>() {
  override fun onBindViewHolder(viewHolder: TodoListOverviewAdapter.ViewHolder, position: Int) {
    todoLists.let {
      val todoList = todoLists[position]
      viewHolder.titleTextView.text = todoList.name

      viewHolder.cardLinearLayout.removeAllViewsInLayout()

      todoList.listItems.forEach {
        createCheckBoxes(viewHolder, it)
      }

      viewHolder.cardView.setOnClickListener {
        onTodoClicked(todoList, todoLists.indexOf(todoList))
      }

      viewHolder.toolbar.setOnClickListener {
        onTodoClicked(todoList, todoLists.indexOf(todoList))
      }

      viewHolder.toolbar.setOnMenuItemClickListener {
        when (it.itemId) {
          R.id.todo_list_delete_menu -> {
            onTodoDeleted(todoList)
            todoLists.removeAt(todoLists.indexOf(todoList))
          }
        }
        true
      }
    }
  }

  private fun createCheckBoxes(viewHolder: ViewHolder, todoItem: TodoItem) {
    val checkBox = AppCompatCheckBox(context)
    checkBox.text = todoItem.title

    if (todoItem.completed) {
      checkBox.paintFlags = checkBox.paintFlags + Paint.STRIKE_THRU_TEXT_FLAG
    }

    checkBox.isChecked = todoItem.completed
    checkBox.isClickable = false
    viewHolder.cardLinearLayout.addView(checkBox)
  }

  override fun getItemCount(): Int = todoLists.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val context = parent.context
    val inflater = LayoutInflater.from(context)

    val todoListItem = inflater.inflate(R.layout.todo_list_overview_list_item, parent, false)
    val cardLinearLayout = todoListItem.findViewById(R.id.todo_list_card_linear_layout) as LinearLayout
    val viewHolder = ViewHolder(todoListItem, cardLinearLayout)

    return viewHolder
  }

  class ViewHolder(itemView: View, val cardLinearLayout: LinearLayout) : RecyclerView.ViewHolder(itemView) {
    val titleTextView by lazy {
      itemView.findViewById(R.id.todo_list_title) as TextView
    }

    val cardView by lazy {
      itemView.findViewById(R.id.todo_list_card_view) as CardView
    }

    val toolbar by lazy {
      itemView.findViewById(R.id.todo_list_list_toolbar) as Toolbar
    }

    init {
      toolbar.inflateMenu(R.menu.todo_list_menu)
    }
  }
}
