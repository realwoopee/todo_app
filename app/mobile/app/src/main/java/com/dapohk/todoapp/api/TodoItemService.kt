package com.dapohk.todoapp.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoItemService {
    @GET("tasks")
    suspend fun getTasks(): List<TodoItem>

    @POST("tasks")
    suspend fun addTask(@Body model: TodoItemModel): TodoItem

    @PUT("tasks")
    suspend fun importTasks(@Body model: List<TodoItem>): List<TodoItem>

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") id: String): TodoItem

    @PATCH("tasks/{id}")
    suspend fun editTask(@Path("id") id: String, @Body model: TodoItemModel): TodoItem

    @DELETE("tasks/{id}")
    suspend fun removeTask(@Path("id") id: String)
}

data class TodoItem(val id: String, val text: String, val isDone: Boolean);

data class TodoItemModel(val text: String? = null, val isDone: Boolean? = null);