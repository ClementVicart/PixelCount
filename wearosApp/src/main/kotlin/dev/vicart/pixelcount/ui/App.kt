package dev.vicart.pixelcount.ui

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dev.vicart.pixelcount.ui.screens.ExpenseGroupListScreen
import dev.vicart.pixelcount.ui.screens.Screens
import dev.vicart.pixelcount.ui.theme.AppTheme

@Composable
fun App() = AppTheme {

    AppScaffold {
        val navController = rememberSwipeDismissableNavController()

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = Screens.EXPENSE_GROUP_LIST
        ) {
            composable(Screens.EXPENSE_GROUP_LIST) {
                ExpenseGroupListScreen()
            }
        }
    }
}