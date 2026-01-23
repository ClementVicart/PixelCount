package dev.vicart.pixelcount.ui

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.vicart.pixelcount.ui.screens.AddExpenseGroupScreen
import dev.vicart.pixelcount.ui.screens.AddExpenseScreen
import dev.vicart.pixelcount.ui.screens.ExpenseDetailScreen
import dev.vicart.pixelcount.ui.screens.ExpenseListScreen
import dev.vicart.pixelcount.ui.screens.Screens
import dev.vicart.pixelcount.ui.theme.AppTheme
import dev.vicart.pixelcount.ui.transition.TransitionAxis
import dev.vicart.pixelcount.ui.transition.rememberMaterialTransition
import dev.vicart.pixelcount.ui.transition.transitionAxisMetadata
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun App() = AppTheme {

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Screens.Expense.List::class)
                    subclass(Screens.AddExpenseGroup::class)
                    subclass(Screens.Expense.Detail::class)
                    subclass(Screens.AddExpense::class)
                }
            }
        },
        Screens.Expense.List
    )

    val materialTransition = rememberMaterialTransition()

    val strategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Screens.Expense.List>(
                metadata = ListDetailSceneStrategy.listPane(sceneKey = Screens.Expense)
            ) {
                ExpenseListScreen(
                    addExpenseGroup = {
                        backStack.add(Screens.AddExpenseGroup())
                    },
                    selectItem = {
                        backStack.add(Screens.Expense.Detail(it))
                    },
                    selectedItem = backStack.findLast { it is Screens.Expense.Detail }?.let {
                        (it as Screens.Expense.Detail).id
                    },
                    onEdit = {
                        backStack.add(Screens.AddExpenseGroup(it))
                    }
                )
            }
            entry<Screens.Expense.Detail>(
                metadata = ListDetailSceneStrategy.detailPane(sceneKey = Screens.Expense)
            ) { entry ->
                ExpenseDetailScreen(
                    item = entry.id,
                    onBack = {
                        backStack.remove(entry)
                    },
                    onEdit = {
                        backStack.add(Screens.AddExpenseGroup(it))
                    },
                    onAddExpense = {
                        backStack.add(Screens.AddExpense(entry.id))
                    },
                    onEditExpense = {
                        backStack.add(Screens.AddExpense(entry.id, it))
                    }
                )
            }
            entry<Screens.AddExpenseGroup>(
                metadata = transitionAxisMetadata(TransitionAxis.Y)
            ) {
                AddExpenseGroupScreen(
                    onBack = {
                        backStack.remove(it)
                    },
                    initial = it.item
                )
            }
            entry<Screens.AddExpense> {
                AddExpenseScreen(
                    onBack = {
                        backStack.remove(it)
                    },
                    itemId = it.itemId,
                    initial = it.initial
                )
            }
        },
        sceneStrategy = strategy,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = { with(materialTransition) { transitionSpec } },
        popTransitionSpec = { with(materialTransition) { popTransitionSpec } },
        predictivePopTransitionSpec = { with(materialTransition) { popTransitionSpec } }
    )
}