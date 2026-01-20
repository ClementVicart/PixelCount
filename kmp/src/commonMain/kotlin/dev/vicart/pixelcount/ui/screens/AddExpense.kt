package dev.vicart.pixelcount.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonGroupMenuState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.add_expense
import dev.vicart.pixelcount.ui.components.BackButton
import dev.vicart.pixelcount.ui.viewmodel.AddExpenseViewModel
import dev.vicart.pixelcount.util.prettyPrint
import org.jetbrains.compose.resources.stringResource
import java.lang.Exception
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddExpenseScreen(
    itemId: Uuid,
    vm: AddExpenseViewModel = viewModel(key = itemId.toString()) { AddExpenseViewModel(itemId) },
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val title by vm.title.collectAsStateWithLifecycle()

            OutlinedTextField(
                value = title,
                onValueChange = { vm.title.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Titre") },
                singleLine = true
            )

            val amount by vm.amount.collectAsStateWithLifecycle()

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    if(it.isEmpty()) {
                        vm.amount.value = ""
                    } else {
                        try {
                            it.toDouble()
                            vm.amount.value = it
                        } catch (_: kotlin.Exception) {}
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Montant") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                trailingIcon = {
                    Icon(Icons.Default.Euro, null)
                }
            )

            Text(
                text = "Payé par",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            val group by vm.expenseGroup.collectAsStateWithLifecycle()

            val paidBy by vm.paidBy.collectAsStateWithLifecycle()

            ButtonGroup(
                overflowIndicator = {
                    ButtonGroupDefaults.OverflowIndicator(
                        menuState = remember { ButtonGroupMenuState() }
                    )
                }
            ) {
                group?.participants?.forEach { participant ->
                    toggleableItem(
                        checked = paidBy == participant,
                        label = participant.name,
                        onCheckedChange = { vm.changePaidBy(participant) },
                        weight = 1f
                    )
                }
            }

            Text(
                text = "Partager avec",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            val sharedWith by vm.sharedWith.collectAsStateWithLifecycle()

            group?.participants?.filterNot { it == paidBy }?.forEach { participant ->
                val containerColor by animateColorAsState(
                    targetValue = if(sharedWith?.contains(participant) == true) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primaryContainer,
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                )
                val amountForParticipant = remember(amount, sharedWith) {
                    if(sharedWith?.contains(participant) == false) return@remember 0.0.prettyPrint
                    return@remember ((amount.toDoubleOrNull() ?: 0.0) / (sharedWith.orEmpty().size + 1)).prettyPrint
                }
                ListItem(
                    headlineContent = { Text(participant.name) },
                    colors = ListItemDefaults.colors().copy(
                        containerColor = containerColor,
                        headlineColor = if(sharedWith?.contains(participant) == true) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onPrimaryContainer,
                        trailingIconColor = if(sharedWith?.contains(participant) == true) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .selectable(selected = sharedWith?.contains(participant) == true) {
                            vm.toggleShareParticipant(participant)
                        },
                    leadingContent = {
                        Checkbox(
                            checked = sharedWith?.contains(participant) == true,
                            onCheckedChange = { vm.toggleShareParticipant(participant) }
                        )
                    },
                    trailingContent = {
                        Text(amountForParticipant)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(Res.string.add_expense)) },
        navigationIcon = {
            BackButton(onBack)
        }
    )
}