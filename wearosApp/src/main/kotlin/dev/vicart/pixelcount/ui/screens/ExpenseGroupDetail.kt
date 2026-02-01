package dev.vicart.pixelcount.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListSubHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TitleCard
import dev.vicart.pixelcount.R
import dev.vicart.pixelcount.shared.utils.prettyPrint
import dev.vicart.pixelcount.ui.viewmodel.ExpenseGroupDetailViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ExpenseGroupDetailScreen(
    id: Uuid,
    vm: ExpenseGroupDetailViewModel = viewModel(key = id.toString()) { ExpenseGroupDetailViewModel(id) }
) {
    val scalingState = rememberScalingLazyListState()

    ScreenScaffold(
        scrollState = scalingState
    ) {

        val group by vm.expenseGroup.collectAsStateWithLifecycle()
        val expenses by vm.expenses.collectAsStateWithLifecycle(emptyList())

        ScalingLazyColumn(
            state = scalingState,
            contentPadding = it,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ListHeader {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = group?.emoji.orEmpty()
                        )
                        Text(
                            text = group?.title ?: ""
                        )
                    }
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ListSubHeader {
                        Text(
                            text = stringResource(R.string.my_expenses),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    val myExpenses by vm.myExpenses.collectAsStateWithLifecycle(0.0)
                    Text(
                        text = group?.currency?.let { myExpenses.prettyPrint(it) }.orEmpty(),
                        style = MaterialTheme.typography.numeralSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ListSubHeader {
                        Text(
                            text = stringResource(R.string.total_expenses),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    val totalExpenses by vm.totalExpenses.collectAsStateWithLifecycle(0.0)
                    Text(
                        text = group?.currency?.let { totalExpenses.prettyPrint(it) }.orEmpty(),
                        style = MaterialTheme.typography.numeralSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                ListSubHeader {
                    Text(
                        text = stringResource(R.string.expenses),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            items(expenses, key = { it.id.toString() }) { expense ->
                TitleCard(
                    onClick = {},
                    title = { Text(expense.label) },
                    subtitle = {
                        Text(
                            text = group?.currency?.let { expense.amount.prettyPrint(it) }.orEmpty()
                        )
                    },
                    time = {
                        Text(expense.datetime.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()
                            .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)))
                    }
                )
            }
        }
    }
}