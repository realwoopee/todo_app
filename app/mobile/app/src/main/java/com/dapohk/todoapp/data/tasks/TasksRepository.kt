package com.dapohk.todoapp.data.tasks

import android.net.Uri
import com.dapohk.todoapp.api.TodoItem
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    suspend fun get(id: String): TodoItem?
    suspend fun get(): List<TodoItem>
    suspend fun edit(id: String, isDone: Boolean? = null, text: String? = null)
    suspend fun add(isDone: Boolean, text: String): String
    suspend fun delete(ids: Set<String>)
    suspend fun import(uri: Uri? = null)
    suspend fun export(uri: Uri? = null)

    fun observeTasks(): Flow<List<TodoItem>>
}