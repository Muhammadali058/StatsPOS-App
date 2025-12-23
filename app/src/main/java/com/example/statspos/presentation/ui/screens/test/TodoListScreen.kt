package com.example.statspos.presentation.ui.screens.test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.utils.showToast
import kotlinx.serialization.Serializable

@Serializable
private sealed interface TodoRoute : NavKey{

    @Serializable
    data object Main: TodoRoute

    @Serializable
    data class Details(val todo: String): TodoRoute
}

@Composable
fun TodoListScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoListViewModel = viewModel(),
) {
    val backStack = rememberNavBackStack(TodoRoute.Main)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider  = entryProvider{
            entry<TodoRoute.Main> {
                Main(
                    viewModel = viewModel
                ){ todo ->
                    backStack.add(TodoRoute.Details(todo))
                }
            }
            entry<TodoRoute.Details> { key ->
                TodoDetailScreen(
                    todo = key.todo
                )
            }
        }
    )


}

@Composable
private fun Main(
    viewModel: TodoListViewModel = viewModel(),
    onTodoClick:(String) -> Unit,
) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(todos){todo ->
            Text(
                text = todo,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTodoClick(todo)
                    }
                    .padding(16.dp)
            )
        }
    }
}