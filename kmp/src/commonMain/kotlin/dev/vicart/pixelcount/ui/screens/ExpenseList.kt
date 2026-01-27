package dev.vicart.pixelcount.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.add_expense_group
import dev.vicart.pixelcount.resources.delete
import dev.vicart.pixelcount.resources.expense_groups
import dev.vicart.pixelcount.resources.import
import dev.vicart.pixelcount.resources.modify
import dev.vicart.pixelcount.resources.no_expense_yet
import dev.vicart.pixelcount.ui.components.ConfirmDeleteGroupExpenseDialog
import dev.vicart.pixelcount.ui.components.EmptyContent
import dev.vicart.pixelcount.ui.viewmodel.ExpenseListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpenseListScreen(
    vm: ExpenseListViewModel = viewModel { ExpenseListViewModel() },
    addExpenseGroup: () -> Unit,
    selectedItem: Uuid? = null,
    selectItem: (Uuid) -> Unit,
    onEdit: (ExpenseGroup) -> Unit,
    closeDetail: (Uuid) -> Unit
) {
    Scaffold(
        floatingActionButton = { AddExpenseGroupFab(onClick = addExpenseGroup) },
        topBar = {
            TopBar(
                onImport = vm::importGroup
            )
        },
        snackbarHost = { SnackBar(vm) }
    ) {

        val expenses by vm.expenses.collectAsStateWithLifecycle()

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(expenses.isEmpty()) {
                item {
                    EmptyContent(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        label = { Text(stringResource(Res.string.no_expense_yet)) }
                    )
                }
            } else {
                items(expenses) {
                    Box {
                        var menuExpanded by remember { mutableStateOf(false) }
                        ListItem(
                            headlineContent = { Text(it.title) },
                            leadingContent = {
                                Text(
                                    text = it.emoji,
                                    fontSize = with(LocalDensity.current) {
                                        IconButtonDefaults.smallIconSize.toSp()
                                    }
                                )
                            },
                            tonalElevation = if(selectedItem == it.id) 4.dp else 1.dp,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .combinedClickable(
                                    onLongClick = { menuExpanded = true },
                                    onClick = { selectItem(it.id) }
                                ),
                        )

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.modify)) },
                                onClick = { onEdit(it) },
                                leadingIcon = { Icon(Icons.Default.Edit, null) }
                            )

                            var deleteDialogVisible by remember { mutableStateOf(false) }
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.delete)) },
                                onClick = { deleteDialogVisible = true },
                                leadingIcon = { Icon(Icons.Default.Delete, null) },
                                colors = MenuDefaults.itemColors().copy(
                                    textColor = MaterialTheme.colorScheme.error,
                                    leadingIconColor = MaterialTheme.colorScheme.error
                                )
                            )

                            ConfirmDeleteGroupExpenseDialog(
                                isVisible = deleteDialogVisible,
                                onDismiss = { deleteDialogVisible = false },
                                onConfirm = {
                                    vm.deleteExpenseGroup(it)
                                    closeDetail(it.id)
                                    deleteDialogVisible = false
                                }
                            )
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    onImport: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(Res.string.expense_groups)) },
        actions = {
            Box {
                var menuExpanded by remember { mutableStateOf(false) }
                FilledTonalIconButton(
                    onClick = { menuExpanded = true },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(Icons.Default.MoreVert, null)
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.import)) },
                        leadingIcon = { Icon(Icons.Default.Download, null) },
                        onClick = onImport
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SnackBar(
    vm: ExpenseListViewModel
) {
    val state = remember { SnackbarHostState() }

    LaunchedEffect(vm) {
        vm.errorChannel.receiveAsFlow().collectLatest {
            state.showSnackbar(getString(it.messageRes))
        }
    }

    SnackbarHost(state)
}