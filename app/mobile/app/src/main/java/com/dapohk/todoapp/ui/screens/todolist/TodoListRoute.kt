package com.dapohk.todoapp.ui.screens.todolist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
        onImportTasks = todoListViewModel::onImport,
        onRefreshTasks = todoListViewModel::onRefresh
    )
}

@Composable
private fun TodoListRoute(
    uiState: TodoListUiState,
    onAddTask: () -> Unit,
    onToggleTask: (id: String) -> Unit,
    onEditTask: (id: String, text: String) -> Unit,
    onSelectTask: (id: String) -> Unit,
    onDeleteSelected: () -> Unit,
    onClearSelected: () -> Unit,
    onExportTasks: () -> Unit,
    onImportTasks: () -> Unit,
    onRefreshTasks: () -> Unit,
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
            onImportTasks,
            onRefreshTasks
        )

        is TodoListUiState.NoTasks -> TodoListEmptyScreen(
            uiState,
            onAddTask,
            onImportTasks,
            onRefreshTasks
        )
    }
}