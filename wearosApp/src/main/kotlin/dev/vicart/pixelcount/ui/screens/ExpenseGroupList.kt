package dev.vicart.pixelcount.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ScreenScaffold
import dev.vicart.pixelcount.ui.viewmodel.ExpenseGroupListViewModel

@Composable
fun ExpenseGroupListScreen(
    vm: ExpenseGroupListViewModel = viewModel()
) {
    val state = rememberTransformingLazyColumnState()

    ScreenScaffold(
        scrollState = state
    ) {
        TransformingLazyColumn(
            state = state,
            contentPadding = it
        ) {
            item {

            }
        }
    }
}