package dev.vicart.pixelcount.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vicart.pixelcount.model.ExpenseGroup
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.created_by
import dev.vicart.pixelcount.resources.my_expenses
import dev.vicart.pixelcount.resources.total_expenses
import dev.vicart.pixelcount.ui.components.BackButton
import dev.vicart.pixelcount.ui.viewmodel.ExpenseDetailViewModel
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal
import kotlin.uuid.Uuid

@Composable
fun ExpenseDetailScreen(
    item: Uuid,
    vm: ExpenseDetailViewModel = viewModel(key = item.toString()) { ExpenseDetailViewModel(item) },
    onBack: () -> Unit,
    onEdit: (ExpenseGroup) -> Unit
) {
    val group by vm.expenseGroup.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopBar(
                group = group,
                onBack = onBack,
                onEdit = { onEdit(group!!) }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "\uD83D\uDCB8",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val myExpenses by vm.myExpenses.collectAsStateWithLifecycle(BigDecimal.ZERO)
                    Text(
                        text = stringResource(Res.string.my_expenses),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${myExpenses}€",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val totalExpenses by vm.totalExpenses.collectAsStateWithLifecycle(BigDecimal.ZERO)
                    Text(
                        text = stringResource(Res.string.total_expenses),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${totalExpenses}€",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    group: ExpenseGroup?,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    TopAppBar(
        title = { Text(group?.title ?: "") },
        subtitle = { Text(stringResource(Res.string.created_by, group?.participants?.firstOrNull { it.mandatory }
            ?.name ?: "")) },
        navigationIcon = {
            BackButton(onBack)
        },
        actions = {
            FilledTonalIconButton(
                onClick = onEdit,
                shapes = IconButtonDefaults.shapes(
                    shape = IconButtonDefaults.smallSquareShape,
                    pressedShape = IconButtonDefaults.smallPressedShape
                )
            ) {
                Icon(Icons.Default.Edit, null)
            }
        }
    )
}