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
import dev.vicart.pixelcount.ui.screens.ExpenseListScreen
import dev.vicart.pixelcount.ui.screens.Screens
import dev.vicart.pixelcount.ui.theme.AppTheme
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
                }
            }
        },
        Screens.Expense.List
    )

    val strategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Screens.Expense.List>(
                metadata = ListDetailSceneStrategy.listPane(sceneKey = Screens.Expense)
            ) {
                ExpenseListScreen(
                    addExpenseGroup = { backStack.add(Screens.AddExpenseGroup) }
                )
            }
            entry<Screens.AddExpenseGroup> {
                AddExpenseGroupScreen(
                    onBack = { backStack.remove(it) }
                )
            }
        },
        sceneStrategy = strategy,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}