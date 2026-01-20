package dev.vicart.pixelcount.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import dev.vicart.pixelcount.model.Expense
import dev.vicart.pixelcount.model.ExpenseGroup
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.balance
import dev.vicart.pixelcount.resources.created_by
import dev.vicart.pixelcount.resources.expenses
import dev.vicart.pixelcount.resources.my_expenses
import dev.vicart.pixelcount.resources.no_expense_yet
import dev.vicart.pixelcount.resources.total_expenses
import dev.vicart.pixelcount.ui.components.BackButton
import dev.vicart.pixelcount.ui.components.EmptyContent
import dev.vicart.pixelcount.ui.viewmodel.ExpenseDetailViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    item: Uuid,
    vm: ExpenseDetailViewModel = viewModel(key = item.toString()) { ExpenseDetailViewModel(item) },
    onBack: () -> Unit,
    onEdit: (ExpenseGroup) -> Unit,
    onAddExpense: () -> Unit
) {
    val group by vm.expenseGroup.collectAsStateWithLifecycle()

    val currentWindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val shouldShowToolbar = remember(currentWindowSizeClass) {
        currentWindowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)
    }

    var scaffoldHeight by remember { mutableStateOf(0.dp) }
    var contentHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val sheetPeekHeight = remember(scaffoldHeight, contentHeight) {
        scaffoldHeight - contentHeight
    }

    val bottomSheetState = rememberStandardBottomSheetState()

    val bottomSheetOffset by produceState(1f, bottomSheetState, sheetPeekHeight) {
        snapshotFlow { bottomSheetState.requireOffset() }.collect {
            value = (it / (sheetPeekHeight.value + 16.dp.value)).coerceIn(0f, 1f)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BottomSheetScaffold(
            sheetContent = {
                DetailSheetContent(
                    vm = vm,
                    group = group
                )
            },
            topBar = {
                TopBar(
                    group = group,
                    onBack = onBack,
                    onEdit = { onEdit(group!!) },
                    shouldShowToolbar = shouldShowToolbar,
                    onAddExpense = onAddExpense
                )
            },
            sheetPeekHeight = max(sheetPeekHeight - 16.dp, 0.dp),
            modifier = Modifier.onGloballyPositioned {
                scaffoldHeight = with(density) { it.size.height.toDp() }
            },
            scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .onGloballyPositioned {
                        contentHeight = with(density) { it.positionInWindow().y.toDp() + it.size.height.toDp() }
                    }
                    .alpha(bottomSheetOffset)
                    .scale(bottomSheetOffset),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = group?.emoji ?: "",
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

        if(shouldShowToolbar) {
            NewPaymentFab(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .safeContentPadding(),
                onClick = onAddExpense
            )
        }
    }
}

@Composable
private fun NewPaymentFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    LargeFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        elevation = FloatingActionButtonDefaults.loweredElevation()
    ) {
        Icon(
            imageVector = Icons.Default.AddCard,
            contentDescription = null,
            modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DetailSheetContent(
    vm: ExpenseDetailViewModel,
    group: ExpenseGroup?
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var selectedTab by rememberSaveable { mutableIntStateOf(0) }
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(stringResource(Res.string.expenses)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(stringResource(Res.string.balance)) }
            )
        }

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                (fadeIn(MotionScheme.expressive().defaultEffectsSpec()) + scaleIn(MotionScheme.expressive().defaultSpatialSpec()))
                    .togetherWith(fadeOut(MotionScheme.expressive().defaultEffectsSpec()) + scaleOut(
                        MotionScheme.expressive().defaultSpatialSpec()))
            }
        ) {
            if(it == 0) {
                ExpensesList(
                    expenses = group?.expenses ?: emptyList()
                )
            } else {
                BalanceList(vm = vm)
            }
        }
    }
}

@Composable
private fun BalanceList(
    vm: ExpenseDetailViewModel
) {
    val balances by vm.balances.collectAsStateWithLifecycle(emptyList())

    if(balances.isEmpty()) {
        EmptyContent(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.balance)) }
        )
    } else {

    }
}

@Composable
private fun ExpensesList(
    modifier: Modifier = Modifier,
    expenses: List<Expense>
) {
    if(expenses.isEmpty()) {
        EmptyContent(
            modifier = modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.no_expense_yet)) }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val expensesGrouped = expenses.groupBy {
                it.datetime.toLocalDateTime(TimeZone.currentSystemDefault()).date }.toList()

            items(expensesGrouped) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = it.first.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )

                    it.second.forEach { expense ->
                        ListItem(
                            headlineContent = { Text(expense.label) }
                        )
                    }
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
    onEdit: () -> Unit,
    shouldShowToolbar: Boolean,
    onAddExpense: () -> Unit
) {
    TopAppBar(
        title = { Text(group?.title ?: "") },
        subtitle = { Text(stringResource(Res.string.created_by, group?.participants?.firstOrNull { it.mandatory }
            ?.name ?: "")) },
        navigationIcon = {
            BackButton(onBack)
        },
        actions = {
            if(!shouldShowToolbar) {
                FilledTonalIconButton(
                    onClick = onAddExpense,
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(Icons.Default.AddCard, null)
                }
            }
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