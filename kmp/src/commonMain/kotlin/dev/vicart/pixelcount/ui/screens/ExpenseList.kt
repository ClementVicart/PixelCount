package dev.vicart.pixelcount.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.add_expense_group
import dev.vicart.pixelcount.resources.expense_groups
import dev.vicart.pixelcount.resources.no_expense_yet
import dev.vicart.pixelcount.ui.components.EmptyContent
import dev.vicart.pixelcount.ui.viewmodel.ExpenseListViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExpenseListScreen(
    vm: ExpenseListViewModel = viewModel { ExpenseListViewModel() },
    addExpenseGroup: () -> Unit
) {
    Scaffold(
        floatingActionButton = { AddExpenseGroupFab(onClick = addExpenseGroup) }
    ) {

        val expenses by vm.expenses.collectAsStateWithLifecycle()

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.expense_groups),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if(expenses.isEmpty()) {
                item {
                    EmptyContent(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        label = { Text(stringResource(Res.string.no_expense_yet)) }
                    )
                }
            } else {
                items(expenses) {

                }
            }
        }
    }
}

@Composable
private fun AddExpenseGroupFab(
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        text = { Text(stringResource(Res.string.add_expense_group)) },
        onClick = onClick,
        expanded = true,
        icon = { Icon(Icons.Default.Add, null) }
    )
}