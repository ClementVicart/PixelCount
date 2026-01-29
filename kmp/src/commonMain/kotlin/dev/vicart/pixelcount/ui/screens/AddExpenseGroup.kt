package dev.vicart.pixelcount.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.add_expense_group
import dev.vicart.pixelcount.resources.john_doe
import dev.vicart.pixelcount.resources.options
import dev.vicart.pixelcount.resources.participant_name
import dev.vicart.pixelcount.resources.participants
import dev.vicart.pixelcount.resources.ski_vacations
import dev.vicart.pixelcount.resources.title
import dev.vicart.pixelcount.resources.you
import dev.vicart.pixelcount.resources.your_name
import dev.vicart.pixelcount.ui.components.BackButton
import dev.vicart.pixelcount.ui.components.EmojiPicker
import dev.vicart.pixelcount.ui.viewmodel.AddExpenseGroupViewModel
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseGroupScreen(
    onBack: () -> Unit,
    initial: ExpenseGroup? = null,
    vm: AddExpenseGroupViewModel = viewModel { AddExpenseGroupViewModel(initial) }
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack,
                vm = vm,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val title by vm.title.collectAsStateWithLifecycle()
                OutlinedTextField(
                    value = title,
                    onValueChange = { vm.title.value = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text(stringResource(Res.string.title)) },
                    placeholder = { Text(stringResource(Res.string.ski_vacations)) },
                    leadingIcon = {
                        val emoji by vm.emoji.collectAsStateWithLifecycle()
                        EmojiPicker(
                            emoji = emoji,
                            onEmojiSelected = { vm.emoji.value = it }
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = MaterialTheme.shapes.small
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .animateContentSize(MaterialTheme.motionScheme.defaultSpatialSpec()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.participants),
                            style = MaterialTheme.typography.titleMedium
                        )

                        val userName by vm.userName.collectAsStateWithLifecycle()
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { vm.userName.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(Res.string.you)) },
                            placeholder = { Text(stringResource(Res.string.your_name)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            )
                        )

                        val participants by vm.participants.collectAsStateWithLifecycle()
                        participants.forEach { participant ->
                            OutlinedTextField(
                                value = participant.name,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                singleLine = true,
                                trailingIcon = {
                                    FilledTonalIconButton(
                                        onClick = { vm.deleteParticipant(participant) },
                                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, null)
                                    }
                                }
                            )
                        }

                        var newParticipantName by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = newParticipantName,
                            onValueChange = { newParticipantName = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text(stringResource(Res.string.participant_name)) },
                            placeholder = { Text(stringResource(Res.string.john_doe)) },
                            trailingIcon = {
                                FilledIconButton(
                                    onClick = {
                                        vm.addParticipant(newParticipantName)
                                        newParticipantName = ""
                                    },
                                    enabled = newParticipantName.isNotBlank(),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Icon(Icons.Default.PersonAdd, null)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    vm.addParticipant(newParticipantName)
                                    newParticipantName = ""
                                }
                            )
                        )
                    }
                }

                Text(
                    text = stringResource(Res.string.options),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                val currency by vm.currency.collectAsStateWithLifecycle()

                var menuExpanded by remember(currency) { mutableStateOf(false) }

                var query by remember(currency) { mutableStateOf(currency.displayName) }

                ExposedDropdownMenuBox(
                    expanded = menuExpanded,
                    onExpandedChange = { menuExpanded = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        leadingIcon = {
                            Text(text = currency.symbol)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        val availableCurrencies by vm.availableCurrencies.collectAsStateWithLifecycle(emptyList())

                        availableCurrencies.filter { it.displayName.contains(query, true) }.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text(currency.displayName) },
                                onClick = { vm.currency.value = currency },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                leadingIcon = {
                                    Text(text = currency.symbol)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    onBack: () -> Unit,
    vm: AddExpenseGroupViewModel,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MediumFlexibleTopAppBar(
        title = { Text(stringResource(Res.string.add_expense_group)) },
        navigationIcon = {
            BackButton(onBack)
        },
        actions = {
            val enabled by vm.canAdd.collectAsStateWithLifecycle(false)
            FilledIconButton(
                onClick = {
                    vm.saveExpenseGroup()
                    onBack()
                },
                shapes = IconButtonDefaults.shapes(),
                modifier = Modifier.size(IconButtonDefaults.smallContainerSize(
                    widthOption = IconButtonDefaults.IconButtonWidthOption.Wide
                )),
                enabled = enabled
            ) {
                Icon(Icons.Default.Check, null)
            }
        },
        scrollBehavior = scrollBehavior
    )
}