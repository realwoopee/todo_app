package com.dapohk.todoapp.data.tasks.impl

import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.dapohk.todoapp.models.Task
import com.dapohk.todoapp.data.tasks.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import kotlin.reflect.typeOf

class DebugTasksRepository : TasksRepository {
    private val tasks = MutableStateFlow(
        listOf(
            Task(UUID.randomUUID(), true, "First"),
            Task(UUID.randomUUID(), false, "Second"),
            Task(UUID.randomUUID(), true, "Third")
        )
    )

    override fun get(id: UUID): Task? = tasks.value.find { it.id == id }

    override fun get(): List<Task> = tasks.value

    override fun edit(id: UUID, isDone: Boolean?, text: String?) {
        val task = get(id) ?: return
        tasks.update {
            it.map { old ->
                if (old == task) Task(
                    task.id,
                    isDone ?: task.isDone,
                    text ?: task.text
                ) else old
            }
        }
    }

    override fun add(isDone: Boolean, text: String): UUID {
        val id = UUID.randomUUID()
        tasks.update { (it + Task(id, isDone, text)) }
        return id
    }

    override fun delete(id: UUID) {
        delete(setOf(id))
    }

    override fun delete(ids: Set<UUID>) {
        val tasksToDelete = tasks.value.filter { it.id in ids }

        tasks.update { (it - tasksToDelete.toSet()) }
    }

    override fun import(uri: Uri?) {
        val path =
            uri ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toUri();

        val state = Environment.getExternalStorageState()
        if (state != Environment.MEDIA_MOUNTED) return

        if (path.path == null) return;

        val file = File(path.path, "data.json")
        if (!file.exists()) return
        file.inputStream()
            .use { input ->
                InputStreamReader(input).use {
                    val data =
                        Gson().fromJson<List<Task>>(it, object : TypeToken<List<Task>>() {}.type)
                    tasks.update { data }
                }
            }
    }

    override fun export(uri: Uri?) {
        val path =
            uri ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toUri();

        val data = Gson().toJson(tasks.value);

        val state = Environment.getExternalStorageState()
        if (state != Environment.MEDIA_MOUNTED) return

        if (path.path == null) return;

        val file = File(path.path, "data.json")
        if (file.exists()) file.delete()
        file.createNewFile()
        file.outputStream()
            .use { of -> PrintWriter(of).use { it.print(data) }; of.flush(); of.close() }
    }

    override fun observeTasks(): Flow<List<Task>> = tasks
}