package com.dapohk.todoapp

import android.app.Application
import com.dapohk.todoapp.data.AppContainer

class TodoListApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this.applicationContext)
    }
}