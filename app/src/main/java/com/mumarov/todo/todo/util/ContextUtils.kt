package com.mumarov.todo.todo.util

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager


fun Context.openKeyboard() {
  val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Context.closeKeyboard(windowToken: IBinder) {
  val inputMethodManager = this.getSystemService(
          Context.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}