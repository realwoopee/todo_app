package com.dapohk.todoapp.ui.screens.todolist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.UUID

@Composable
fun TodoListRoute(todoListViewModel: TodoListViewModel) {
    val uiState by todoListViewModel.uiState.collectAsStateWithLifecycle()

    TodoListRoute(
        uiState = uiState,
        onAddTask = todoListViewModel::onNew,
        onToggleTask = todoListViewModel::onToggle,
        onDeleteSelected = todoListViewModel::onDeleteSelected,
        onSelectTask = todoListViewModel::onSelect,
        onEditTask = todoListViewModel::onEdit,
        onClearSelected = todoListViewModel::onSelectionClear,
        onExportTasks = todoListViewModel::onExport,
        onImportTasks = todoListViewModel::onImport
    )
}

@Composable
private fun TodoListRoute(
    uiState: TodoListUiState,
    onAddTask: () -> Unit,
    onToggleTask: (id: UUID) -> Unit,
    onEditTask: (id: UUID, text: String) -> Unit,
    onSelectTask: (id: UUID) -> Unit,
    onDeleteSelected: () -> Unit,
    onClearSelected: () -> Unit,
    onExportTasks: () -> Unit,
    onImportTasks: () -> Unit
) {
    when (uiState) {
        is TodoListUiState.HasTasks -> TodoListTasksScreen(
            uiState,
            onAddTask,
            onToggleTask,
            onDeleteSelected,
            onSelectTask,
            onEditTask,
            onClearSelected,
            onExportTasks,
            onImportTasks
        )

        is TodoListUiState.NoTasks -> TodoListEmptyScreen(
            uiState,
            onAddTask,
            onImportTasks
        )
    }
}