package com.dapohk.todoapp.ui.screens.todolist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.dapohk.todoapp.R

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun TodoListEmptyScreen(
    uiState: TodoListUiState.NoTasks,
    onAddTask: () -> Unit,
    onImportTasks: () -> Unit,
    onRefreshTasks: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = onRefreshTasks
    )
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name)
                    )
                },
                actions = {
                    IconButton(onClick = onAddTask) {
                        Text(text = "A")
                    }
                    IconButton(onClick = onImportTasks) {
                        Text(text = "I")
                    }
                }
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .verticalScroll(rememberScrollState())
                .clickable { onAddTask() },
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.add_task))

            PullRefreshIndicator(
                refreshing = false,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun TodoListTasksScreen(
    uiState: TodoListUiState.HasTasks,
    onAddTask: () -> Unit,
    onToggleTask: (id: String) -> Unit,
    onDeleteSelected: () -> Unit,
    onSelectTask: (id: String) -> Unit,
    onEditTask: (id: String, text: String) -> Unit,
    onClearSelected: () -> Unit,
    onExportTasks: () -> Unit,
    onImportTasks: () -> Unit,
    onRefreshTasks: () -> Unit
) {
    var currentTaskInEditId: String? by remember { mutableStateOf(null) }
    var currentTaskInEditText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = onRefreshTasks
    )

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name)
                    )
                },
                actions = {
                    IconButton(onClick = onImportTasks) {
                        Text(text = "I")
                    }
                    IconButton(onClick = onExportTasks) {
                        Text(text = "E")
                    }
                    if (uiState.selectedTasks.isEmpty())
                        IconButton(onClick = onAddTask) {
                            Text(text = "A")
                        }
                    else {
                        IconButton(onClick = onDeleteSelected) {
                            Text(text = "D")
                        }
                    }
                }
            )
        }) { innerPadding ->

        val haptic = LocalHapticFeedback.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
                    .clickable {
                        currentTaskInEditId = null
                        currentTaskInEditText = ""
                    }
            ) {
                uiState.tasks.map { task ->
                    item {
                        Row(
                            modifier = (
                                    if (task in uiState.selectedTasks)
                                        Modifier.background(Color.LightGray)
                                    else
                                        Modifier)
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        if (uiState.selectedTasks.any())
                                            onSelectTask(task.id)
                                        else {
                                            currentTaskInEditText = task.text
                                            currentTaskInEditId = task.id
                                        }
                                    },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onSelectTask(task.id)
                                    })
                        ) {
                            Checkbox(
                                checked = task.isDone,
                                onCheckedChange = { onToggleTask(task.id) })
                            if (task.id != currentTaskInEditId)
                                Text(
                                    text = task.text,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            else {
                                TextField(
                                    modifier = Modifier.focusRequester(focusRequester),
                                    value = currentTaskInEditText,
                                    onValueChange = { value -> currentTaskInEditText = value },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        onEditTask(task.id, currentTaskInEditText)
                                        currentTaskInEditId = null
                                        currentTaskInEditText = ""
                                    })
                                )
                                LaunchedEffect(Unit) {
                                    focusRequester.requestFocus()
                                }
                            }
                        }
                    }
                }
            }


            PullRefreshIndicator(
                refreshing = false,
                state = pullRefreshState,
                modifier = Modifier.align(alignment = Alignment.TopCenter)
            )

            BackHandler {
                onClearSelected()
                currentTaskInEditId = null
                currentTaskInEditText = ""
            }
        }
    }
}

