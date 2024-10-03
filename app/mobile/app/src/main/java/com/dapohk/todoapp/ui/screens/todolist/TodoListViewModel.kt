package com.dapohk.todoapp.ui.screens.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dapohk.todoapp.api.TodoItem
import com.dapohk.todoapp.data.tasks.TasksRepository
import com.dapohk.todoapp.data.tasks.impl.DebugTasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URL

class TodoListViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    private val _state = MutableStateFlow(TodoListViewModelState())
    val uiState = _state.map(TodoListViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, _state.value.toUiState())

    init {
        viewModelScope.launch {
            tasksRepository.observeTasks().collect { tasks ->
                val newSelectedTasks =
                    _state.value.selectedTasks - (_state.value.tasks - tasks.toSet()).toSet()
                _state.update { it.copy(tasks = tasks, selectedTasks = newSelectedTasks) }
            }
        }
        viewModelScope.launch {
            // прогрев
            tasksRepository.get()
        }
    }

    fun onBackendUrlUpdate(url: String) {
        (tasksRepository as DebugTasksRepository).updateBackendUrl(url.trimEnd('/'));
    }

    fun onRefresh() {
        viewModelScope.launch {
            tasksRepository.get()
        }
    }

    fun onNew() {
        viewModelScope.launch {
            tasksRepository.add(false, "")
        }
    }

    fun onDeleteSelected() {
        viewModelScope.launch {
            tasksRepository.delete(_state.value.selectedTasks.map { it.id }.toSet())
        }
    }

    fun onEdit(id: String, text: String) {
        viewModelScope.launch {
            tasksRepository.edit(id, text = text)
        }
    }

    fun onSelect(id: String) {
        viewModelScope.launch {
            val task = tasksRepository.get(id) ?: return@launch
            _state.update { it.copy(selectedTasks = if (task in _state.value.selectedTasks) it.selectedTasks - task else it.selectedTasks + task) }
        }
    }

    fun onSelectionClear() {
        _state.update { it.copy(selectedTasks = emptyList()) }
    }

    fun onToggle(id: String) {
        viewModelScope.launch {
            val task = tasksRepository.get(id) ?: return@launch
            tasksRepository.edit(id, isDone = !task.isDone)
        }
    }

    fun onExport() {
        viewModelScope.launch {
            tasksRepository.export()
        }
    }

    fun onImport() {
        viewModelScope.launch {
            tasksRepository.import()
        }
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
    val tasks: List<TodoItem> = emptyList(),
    val selectedTasks: List<TodoItem> = emptyList()
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
        val tasks: List<TodoItem>,
        val selectedTasks: List<TodoItem>
    ) : TodoListUiState()
}