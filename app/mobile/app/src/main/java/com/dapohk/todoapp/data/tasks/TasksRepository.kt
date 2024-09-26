package com.dapohk.todoapp.data.tasks

import android.net.Uri
import com.dapohk.todoapp.models.Task
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TasksRepository {
    fun get(id: UUID): Task?
    fun get(): List<Task>
    fun edit(id: UUID, isDone: Boolean? = null, text: String? = null)
    fun add(isDone: Boolean, text: String): UUID
    fun delete(id: UUID)
    fun delete(ids: Set<UUID>)
    fun import(uri: Uri? = null)
    fun export(uri: Uri? = null)

    fun observeTasks(): Flow<List<Task>>
}