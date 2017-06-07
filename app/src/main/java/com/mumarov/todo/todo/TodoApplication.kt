package com.mumarov.todo.todo

import android.app.Application
import com.mumarov.todo.todo.di.AndroidModule
import com.mumarov.todo.todo.di.AppComponent
import com.mumarov.todo.todo.di.DaggerAppComponent
import timber.log.Timber


class TodoApplication: Application() {
  val appComponent: AppComponent = DaggerAppComponent.builder()
          .androidModule(AndroidModule(this))
          .build()

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }


  }


}