package com.dapohk.todoapp.data

import android.content.Context
import com.dapohk.todoapp.data.tasks.impl.DebugTasksRepository


class AppContainer(private val context: Context) {
    val tasksRepository = DebugTasksRepository()
}