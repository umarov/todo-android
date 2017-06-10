package com.mumarov.todo.todo.ui.todo_list_detail

import android.content.Context
import android.graphics.Paint
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.mumarov.todo.todo.R
import com.mumarov.todo.todo.database.entities.TodoItem
import com.mumarov.todo.todo.util.closeKeyboard
import com.mumarov.todo.todo.util.openKeyboard


class TodoListDetailAdapter(
        val todoItems: List<TodoItem>?,
        val context: Context,
        inline val onTodoItemAction: (todoItem: TodoItem, actionType: Int) -> Unit): RecyclerView.Adapter<TodoListDetailAdapter.ViewHolder>() {

  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    todoItems?.let {
      val todoItem = todoItems[position]

      if (todoItem.title.isEmpty()) {
        viewHolder.todoListDetailItemTextView.performClick()

        viewHolder.toggleEditCreate()

        viewHolder.todoListDetailItemEditTextLayout.requestFocus()
        context.openKeyboard()

        viewHolder.todoListDetailItemEditTextLayout.setOnFocusChangeListener { _, hasFocus ->
          if (!hasFocus) {
            createTodoItem(viewHolder, todoItem)
          }
        }

        viewHolder.todoListDetailItemEditSaveButton.setOnClickListener {
          createTodoItem(viewHolder, todoItem)
        }
      } else {
        viewHolder.todoListDetailItemTextView.text = todoItem.title

        if (todoItem.completed) {
          viewHolder.todoListDetailItemTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        } else {
          viewHolder.todoListDetailItemTextView.paintFlags = 0
        }

        viewHolder.todoListDetailItemCheckBox.isChecked = todoItem.completed

        setUpItemTextViewListener(viewHolder, todoItem)

        viewHolder.todoListDetailDeletItemButton.setOnClickListener {
          onTodoItemAction(todoItem, TodoItem.DELETE)
        }

        viewHolder.todoListDetailItemCheckBox.setOnCheckedChangeListener { _, isChecked ->
          todoItem.completed = isChecked
          onTodoItemAction(todoItem, TodoItem.UPDATE)
        }
      }

    }
  }

  private fun setUpItemTextViewListener(viewHolder: ViewHolder, todoItem: TodoItem) {
    viewHolder.todoListDetailItemTextView.setOnClickListener {
      viewHolder.todoListDetailItemEditTextLayout.setText(todoItem.title)

      viewHolder.toggleEditCreate()

      viewHolder.todoListDetailItemEditTextLayout.requestFocus()
      context.openKeyboard()

      viewHolder.todoListDetailItemEditTextLayout.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
          updateTodoItem(viewHolder, todoItem)
        }
      }

      viewHolder.todoListDetailItemEditSaveButton.setOnClickListener {
        updateTodoItem(viewHolder, todoItem)
      }
    }
  }

  private fun updateTodoItem(viewHolder: ViewHolder, todoItem: TodoItem) {
    todoItemAction(viewHolder, todoItem) {
      onTodoItemAction(it, TodoItem.UPDATE)
    }
  }

  private fun createTodoItem(viewHolder: ViewHolder, todoItem: TodoItem) {
    todoItemAction(viewHolder, todoItem) {
      onTodoItemAction(it, TodoItem.CREATE)
    }
  }

  private inline fun todoItemAction(viewHolder: ViewHolder, todoItem: TodoItem, action: (todoItem: TodoItem) -> Unit ) {
    val textValue = viewHolder.todoListDetailItemEditTextLayout.text.toString()

    if (textValue != todoItem.title) {
      todoItem.title = viewHolder.todoListDetailItemEditTextLayout.text.toString()
      action(todoItem)
    }

    context.closeKeyboard(viewHolder.todoListDetailItemEditTextLayout.windowToken)
    viewHolder.toggleEditCreate()
  }

  override fun getItemCount(): Int {
    if (todoItems != null) {
      return todoItems.size
    } else {
      return 0
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val context = parent.context
    val inflater = LayoutInflater.from(context)
    val todoListDetailItems = inflater.inflate(R.layout.todo_list_detail_list_item, parent, false)

    return ViewHolder(todoListDetailItems)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val todoListDetailItemTextView: TextView by lazy {
      itemView.findViewById(R.id.todo_list_detail_item_text_view) as TextView
    }

    val todoListDetailItemCheckBox: CheckBox by lazy {
      itemView.findViewById(R.id.todo_list_detail_item_check_box) as CheckBox
    }

    val todoListDetailItemEditLayout: ConstraintLayout by lazy {
      itemView.findViewById(R.id.todo_list_item_edit_layout) as ConstraintLayout
    }

    val todoListDetailItemEditSaveButton: AppCompatImageButton by lazy {
      itemView.findViewById(R.id.todo_list_item_edit_save_button) as AppCompatImageButton
    }

    val todoListDetailItemEditTextLayout: TextInputEditText by lazy {
      itemView.findViewById(R.id.todo_list_item_edit_input_edit_text) as TextInputEditText
    }

    val todoListDetailDeletItemButton: AppCompatImageButton by lazy {
      itemView.findViewById(R.id.todo_list_item_delete_button) as AppCompatImageButton
    }

    val todoListDetailItemViewLayout: ConstraintLayout by lazy {
      itemView.findViewById(R.id.todo_list_item_view_layout) as ConstraintLayout
    }

    fun toggleEditCreate() {
      if (todoListDetailItemViewLayout.visibility == View.VISIBLE &&
              todoListDetailItemEditLayout.visibility == View.GONE) {
        todoListDetailItemViewLayout.visibility = View.GONE
        todoListDetailItemEditLayout.visibility = View.VISIBLE
      } else {
        todoListDetailItemViewLayout.visibility = View.VISIBLE
        todoListDetailItemEditLayout.visibility = View.GONE
      }
    }
  }
}
