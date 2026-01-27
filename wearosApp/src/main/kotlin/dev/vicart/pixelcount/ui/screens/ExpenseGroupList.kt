package dev.vicart.pixelcount.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import dev.vicart.pixelcount.ui.viewmodel.ExpenseGroupListViewModel

@Composable
fun ExpenseGroupListScreen(
    vm: ExpenseGroupListViewModel = viewModel()
) {
    val state = rememberTransformingLazyColumnState()

    ScreenScaffold(
        scrollState = state
    ) {

        val expenseGroups by vm.expenseGroups.collectAsStateWithLifecycle()

        TransformingLazyColumn(
            state = state,
            contentPadding = it,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            if(expenseGroups.isEmpty()) {
                item {
                    Text(
                        text = "Nothing to show",
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            } else {

            }
        }
    }
}