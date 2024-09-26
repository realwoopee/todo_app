package com.dapohk.todoapp.ui.screens.todolist

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dapohk.todoapp.data.tasks.TasksRepository
import com.dapohk.todoapp.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class TodoListViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    private val _state = MutableStateFlow(TodoListViewModelState())
    val uiState = _state.map(TodoListViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, _state.value.toUiState())

    init {
        _state.update { it.copy(tasks = tasksRepository.get()) }

        viewModelScope.launch {
            tasksRepository.observeTasks().collect { tasks ->
                val newSelectedTasks =
                    _state.value.selectedTasks - (_state.value.tasks - tasks.toSet()).toSet()
                _state.update { it.copy(tasks = tasks, selectedTasks = newSelectedTasks) }
            }
        }
    }

    fun onNew() {
        tasksRepository.add(false, "")
    }

    fun onDeleteSelected() {
        tasksRepository.delete(_state.value.selectedTasks.map { it.id }.toSet())
    }

    fun onEdit(id: UUID, text: String) {
        tasksRepository.edit(id, text = text)
    }

    fun onSelect(id: UUID) {
        val task = tasksRepository.get(id) ?: return
        _state.update { it.copy(selectedTasks = if (task in _state.value.selectedTasks) it.selectedTasks - task else it.selectedTasks + task) }
    }

    fun onSelectionClear() {
        _state.update { it.copy(selectedTasks = emptyList()) }
    }

    fun onToggle(id: UUID) {
        val task = tasksRepository.get(id) ?: return
        tasksRepository.edit(id, isDone = !task.isDone)
    }

    fun onExport() {
        tasksRepository.export()
    }

    fun onImport() {
        tasksRepository.import()
    }

    companion object {
        fun provideFactory(
            tasksRepository: TasksRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoListViewModel(tasksRepository) as T
                }
            }
    }
}

private data class TodoListViewModelState(
    val tasks: List<Task> = emptyList(),
    val selectedTasks: List<Task> = emptyList()
) {
    fun toUiState(): TodoListUiState =
        if (tasks.isNotEmpty()) {
            TodoListUiState.HasTasks(tasks, selectedTasks)
        } else {
            TodoListUiState.NoTasks
        }
}

sealed class TodoListUiState {
    data object NoTasks : TodoListUiState()

    data class HasTasks(
        val tasks: List<Task>,
        val selectedTasks: List<Task>
    ) : TodoListUiState()
}