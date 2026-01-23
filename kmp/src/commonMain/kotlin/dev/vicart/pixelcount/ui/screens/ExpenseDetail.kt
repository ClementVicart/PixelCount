package dev.vicart.pixelcount.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import dev.vicart.pixelcount.model.Expense
import dev.vicart.pixelcount.model.ExpenseGroup
import dev.vicart.pixelcount.model.PaymentTypeEnum
import dev.vicart.pixelcount.resources.Res
import dev.vicart.pixelcount.resources.balance
import dev.vicart.pixelcount.resources.created_by
import dev.vicart.pixelcount.resources.delete
import dev.vicart.pixelcount.resources.expenses
import dev.vicart.pixelcount.resources.my_expenses
import dev.vicart.pixelcount.resources.no_balance_required
import dev.vicart.pixelcount.resources.no_expense_yet
import dev.vicart.pixelcount.resources.owes_to
import dev.vicart.pixelcount.resources.select_or_create_a_group
import dev.vicart.pixelcount.resources.total_expenses
import dev.vicart.pixelcount.resources.transfer
import dev.vicart.pixelcount.ui.components.BackButton
import dev.vicart.pixelcount.ui.components.ConfirmDeleteGroupExpenseDialog
import dev.vicart.pixelcount.ui.components.EmptyContent
import dev.vicart.pixelcount.ui.viewmodel.ExpenseDetailViewModel
import dev.vicart.pixelcount.util.prettyPrint
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    item: Uuid,
    vm: ExpenseDetailViewModel = viewModel(key = item.toString()) { ExpenseDetailViewModel(item) },
    onBack: () -> Unit,
    onEdit: (ExpenseGroup) -> Unit,
    onAddExpense: () -> Unit,
    onEditExpense: (Expense) -> Unit
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
                    group = group,
                    onExpenseClicked = onEditExpense
                )
            },
            topBar = {
                TopBar(
                    group = group,
                    onBack = onBack,
                    onEdit = { onEdit(group!!) },
                    shouldShowToolbar = shouldShowToolbar,
                    onAddExpense = onAddExpense,
                    onDeleteExpenseGroup = {
                        vm.deleteExpenseGroup()
                        onBack()
                    }
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
                    text = group?.emoji.orEmpty(),
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
                        val myExpenses by vm.myExpenses.collectAsStateWithLifecycle(0.0)
                        Text(
                            text = stringResource(Res.string.my_expenses),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = group?.let { myExpenses.prettyPrint(it.currency) }.orEmpty(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val totalExpenses by vm.totalExpenses.collectAsStateWithLifecycle(0.0)
                        Text(
                            text = stringResource(Res.string.total_expenses),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = group?.let { totalExpenses.prettyPrint(it.currency) }.orEmpty(),
                            style = MaterialTheme.typography.displaySmall,
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
    group: ExpenseGroup?,
    onExpenseClicked: (Expense) -> Unit
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
                    onExpenseClicked = onExpenseClicked,
                    group = group,
                    vm = vm
                )
            } else {
                BalanceList(
                    vm = vm,
                    group = group
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BalanceList(
    vm: ExpenseDetailViewModel,
    group: ExpenseGroup?
) {
    val balances by vm.balances.collectAsStateWithLifecycle(emptyList())

    if(balances.isEmpty() || group == null) {
        EmptyContent(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.no_balance_required)) }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(balances) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("${it.from.name} ")
                                }
                                append(stringResource(Res.string.owes_to))
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(" ${it.to.name} ")
                                }
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)) {
                                    append(it.amount.prettyPrint(group.currency))
                                }
                            }
                        )
                    },
                    modifier = Modifier.clip(MaterialTheme.shapes.small),
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { vm.completeTransfer(it) },
                            shapes = IconButtonDefaults.shapes(),
                            modifier = Modifier.size(IconButtonDefaults.smallContainerSize(
                                widthOption = IconButtonDefaults.IconButtonWidthOption.Narrow
                            ))
                        ) {
                            Icon(Icons.Default.Check, null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ExpensesList(
    modifier: Modifier = Modifier,
    group: ExpenseGroup?,
    vm: ExpenseDetailViewModel,
    onExpenseClicked: (Expense) -> Unit
) {
    if(group?.expenses.isNullOrEmpty()) {
        EmptyContent(
            modifier = modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.no_expense_yet)) }
        )
    } else {
        val expensesGrouped by vm.expenses.collectAsStateWithLifecycle(emptyMap())

        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(expensesGrouped.toList()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = it.first.prettyPrint,
                        style = MaterialTheme.typography.titleMedium
                    )

                    it.second.forEach { expense ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = buildString {
                                        if(expense.type == PaymentTypeEnum.PAYMENT) append("- ")
                                        if(expense.type == PaymentTypeEnum.REFUND) append("+ ")
                                        append(expense.amount.let(::abs).prettyPrint(group.currency))
                                    },
                                    color = when(expense.type) {
                                        PaymentTypeEnum.PAYMENT -> MaterialTheme.colorScheme.error
                                        PaymentTypeEnum.REFUND -> Color.Green
                                        else -> LocalContentColor.current
                                    }
                                )
                            },
                            overlineContent = {
                                Text(
                                    text = if(expense.type == PaymentTypeEnum.TRANSFER)
                                        stringResource(Res.string.transfer)
                                    else
                                        expense.label
                                )
                            },
                            modifier = Modifier.clip(MaterialTheme.shapes.small).clickable {
                                onExpenseClicked(expense)
                            },
                            supportingContent = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.tertiary
                                    ) {
                                        Text(expense.paidBy.name)
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowRightAlt,
                                        contentDescription = null
                                    )
                                    expense.sharedWith.forEach { participant ->
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        ) {
                                            Text(participant.name)
                                        }
                                    }
                                }
                            }
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
    onAddExpense: () -> Unit,
    onDeleteExpenseGroup: () -> Unit
) {
    TopAppBar(
        title = { Text(group?.title.orEmpty()) },
        subtitle = { Text(stringResource(Res.string.created_by, group?.participants?.firstOrNull { it.mandatory }
            ?.name.orEmpty())) },
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

            Box {
                var menuExpanded by remember { mutableStateOf(false) }
                FilledTonalIconButton(
                    onClick = { menuExpanded = true },
                    shapes = IconButtonDefaults.shapes(
                        shape = IconButtonDefaults.smallSquareShape,
                        pressedShape = IconButtonDefaults.smallPressedShape
                    ),
                    modifier = Modifier.size(IconButtonDefaults.smallContainerSize(
                        widthOption = IconButtonDefaults.IconButtonWidthOption.Narrow
                    ))
                ) {
                    Icon(Icons.Default.MoreVert, null)
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    var deleteDialogVisible by remember { mutableStateOf(false) }

                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.delete)) },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        onClick = { deleteDialogVisible = true },
                        colors = MenuDefaults.itemColors().copy(
                            textColor = MaterialTheme.colorScheme.error,
                            leadingIconColor = MaterialTheme.colorScheme.error
                        )
                    )

                    ConfirmDeleteGroupExpenseDialog(
                        isVisible = deleteDialogVisible,
                        onDismiss = { deleteDialogVisible = false },
                        onConfirm = onDeleteExpenseGroup
                    )
                }
            }
        }
    )
}

@Composable
fun UnselectedExpenseGroupDetail() {
    EmptyContent(
        modifier = Modifier.fillMaxSize(),
        label = { Text(stringResource(Res.string.select_or_create_a_group)) }
    )
}