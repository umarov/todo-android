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
import io.reactivex.subjects.PublishSubject
import android.os.IBinder
import android.view.inputmethod.InputMethodManager


class TodoListDetailAdapter(val todoItems: List<TodoItem>?, val context: Context): RecyclerView.Adapter<TodoListDetailAdapter.ViewHolder>() {
  private val onTodoItemUpdated = PublishSubject.create<TodoItem>()
  private val onTodoItemDeleted = PublishSubject.create<TodoItem>()

  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    todoItems?.let {
      val todoItem = todoItems[position]
      viewHolder.todoListDetailItemTextView.text = todoItem.title
      if (todoItem.completed) {
        viewHolder.todoListDetailItemTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
      } else {
        viewHolder.todoListDetailItemTextView.paintFlags = 0
      }
      viewHolder.todoListDetailItemCheckBox.isChecked = todoItem.completed

      viewHolder.todoListDetailItemTextView.setOnClickListener {
        viewHolder.todoListDetailItemEditTextLayout.setText(todoItem.title)

        viewHolder.todoListDetailItemViewLayout.visibility = View.GONE
        viewHolder.todoListDetailItemEditLayout.visibility = View.VISIBLE

        viewHolder.todoListDetailItemEditTextLayout.requestFocus()
        openKeyboard()

        viewHolder.todoListDetailItemEditTextLayout.setOnFocusChangeListener { _, hasFocus ->
          if (!hasFocus) {
            viewHolder.todoListDetailItemEditSaveButton.performClick()
          }
        }

        viewHolder.todoListDetailItemEditSaveButton.setOnClickListener {
          val textValue = viewHolder.todoListDetailItemEditTextLayout.text.toString()

          if (textValue != todoItem.title) {
            todoItem.title = viewHolder.todoListDetailItemEditTextLayout.text.toString()
            onTodoItemUpdated.onNext(todoItem)
          }

          viewHolder.todoListDetailItemViewLayout.visibility = View.VISIBLE
          viewHolder.todoListDetailItemEditLayout.visibility = View.GONE
          closeKeyboard(viewHolder.todoListDetailItemEditTextLayout.windowToken)
        }
      }

      viewHolder.todoListDetailDeletItemButton.setOnClickListener {
        onTodoItemDeleted.onNext(todoItem)
      }

      viewHolder.todoListDetailItemCheckBox.setOnCheckedChangeListener { _, isChecked ->
        todoItem.completed = isChecked
        onTodoItemUpdated.onNext(todoItem)
      }
    }
  }

  fun getTodoItemUpdated(): PublishSubject<TodoItem> = onTodoItemUpdated
  fun getTodoItemDeleted(): PublishSubject<TodoItem> = onTodoItemDeleted

  private fun openKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
  }

  private fun closeKeyboard(windowToken: IBinder) {
    val imm = context.getSystemService(
            Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
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


  companion object class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
  }
}