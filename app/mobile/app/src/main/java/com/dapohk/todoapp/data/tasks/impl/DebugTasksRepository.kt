package com.dapohk.todoapp.data.tasks.impl

import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.dapohk.todoapp.api.TodoItem
import com.dapohk.todoapp.api.TodoItemModel
import com.dapohk.todoapp.api.TodoItemService
import com.dapohk.todoapp.data.tasks.TasksRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.URL

class DebugTasksRepository(
    private var itemService: TodoItemService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://10.0.2.2:5166/")
        .build().create(TodoItemService::class.java)
) : TasksRepository {

    private val tasks: MutableStateFlow<List<TodoItem>> = MutableStateFlow(emptyList())

    fun updateBackendUrl(url: String) {
        itemService = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build().create(TodoItemService::class.java)
    }

    override suspend fun get(id: String): TodoItem {
        val task = itemService.getTask(id)
        tasks.update { it.map { i -> if (i.id == task.id) task else i } }
        return task
    }

    override suspend fun get(): List<TodoItem> {
        val newTasks = itemService.getTasks()
        tasks.update { newTasks }
        return newTasks
    }

    override suspend fun edit(id: String, isDone: Boolean?, text: String?) {
        val task = itemService.editTask(id, TodoItemModel(text, isDone))
        tasks.update { it.map { i -> if (i.id == task.id) task else i } }
    }

    override suspend fun add(isDone: Boolean, text: String): String {
        val task = itemService.addTask(TodoItemModel(text, isDone))
        get()
        return task.id
    }

    override suspend fun delete(ids: Set<String>) = coroutineScope {
        ids.map { id -> async { itemService.removeTask(id) } }.awaitAll()
        get()
        return@coroutineScope
    }

    override suspend fun import(uri: Uri?) {
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
                        Gson().fromJson<List<TodoItem>>(
                            it,
                            object : TypeToken<List<TodoItem>>() {}.type
                        )
                    val newTasks = itemService.importTasks(data)
                    tasks.update { newTasks }
                }
            }
    }

    override suspend fun export(uri: Uri?) {
        val path =
            uri ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toUri();

        val data = Gson().toJson(tasks.value);

        val state = Environment.getExternalStorageState()
        if (state != Environment.MEDIA_MOUNTED) return

        if (path.path == null) return;

        val file = File(path.path, "data.json")
        if (file.exists()) file.delete()
        withContext(Dispatchers.IO) {
            file.createNewFile()
        }
        file.outputStream()
            .use { of -> PrintWriter(of).use { it.print(data) }; of.flush(); of.close() }
    }

    override fun observeTasks(): Flow<List<TodoItem>> = tasks
}