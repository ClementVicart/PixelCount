package dev.vicart.pixelcount.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import dev.vicart.pixelcount.ui.viewmodel.ExpenseGroupListViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ExpenseGroupListScreen(
    vm: ExpenseGroupListViewModel = viewModel(),
    navigateToExpenseGroup: (Uuid) -> Unit
) {
    val state = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

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
                items(expenseGroups, key = { it.id.toString() }) {
                    FilledTonalButton(
                        label = { Text(it.title) },
                        onClick = { navigateToExpenseGroup(it.id) },
                        icon = {
                            Text(
                                text = it.emoji,
                                fontSize = with(LocalDensity.current) {
                                    ButtonDefaults.IconSize.toSp()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxSize().transformedHeight(this, transformationSpec)
                    )
                }
            }
        }
    }
}