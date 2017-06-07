package com.mumarov.todo.todo.ui.todo_list_overview

import android.content.Context
import android.graphics.Paint
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.mumarov.todo.todo.R
import com.mumarov.todo.todo.database.entities.TodoList
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class TodoListOverviewAdapter(val todoLists: List<TodoList>?, val context: Context): RecyclerView.Adapter<TodoListOverviewAdapter.ViewHolder>() {
  private val onClickSubject = PublishSubject.create<TodoList>()

  override fun onBindViewHolder(viewHolder: TodoListOverviewAdapter.ViewHolder, position: Int) {
    todoLists?.let {
      val todoList = todoLists[position]
      viewHolder.titleTextView.text = todoList.name

      todoList.listItems.forEach {
        val checkBox = AppCompatCheckBox(context)
        checkBox.text = it.title

        if (it.completed) {
          checkBox.paintFlags = checkBox.paintFlags + Paint.STRIKE_THRU_TEXT_FLAG
        }

        checkBox.isChecked = it.completed
        checkBox.isClickable = false
        viewHolder.cardLinearLayout.addView(checkBox)
      }

      viewHolder.cardLinearLayout.setOnClickListener {
        onClickSubject.onNext(todoList)
      }
    }
  }

  override fun getItemCount(): Int {
    if (todoLists != null ){
      return todoLists.size
    } else { return 0 }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val context = parent.context
    val inflater = LayoutInflater.from(context)

    val todoListItem = inflater.inflate(R.layout.todo_list_overview_list_item, parent, false) as CardView
    val cardLinearLayout = todoListItem.findViewById(R.id.todo_list_card_linear_layout) as LinearLayout
    val viewHolder = ViewHolder(todoListItem, cardLinearLayout)

    return viewHolder
  }

  fun getClickEvent(): Observable<TodoList> {
    return onClickSubject
  }

  companion object class ViewHolder(itemView: CardView, val cardLinearLayout: LinearLayout) : RecyclerView.ViewHolder(itemView) {
    val titleTextView by lazy {
      itemView.findViewById(R.id.todo_list_title) as TextView
    }
  }
}
