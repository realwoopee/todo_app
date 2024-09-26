package com.dapohk.todoapp.models

import java.util.UUID

data class Task(val id: UUID, var isDone: Boolean, var text: String)
