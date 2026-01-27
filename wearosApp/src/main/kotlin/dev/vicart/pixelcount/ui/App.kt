package dev.vicart.pixelcount.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import dev.vicart.pixelcount.ui.screens.ExpenseGroupDetailScreen
import dev.vicart.pixelcount.ui.screens.ExpenseGroupListScreen
import dev.vicart.pixelcount.ui.screens.Screens
import dev.vicart.pixelcount.ui.theme.AppTheme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun App() = AppTheme {

    AppScaffold {
        val navController = rememberSwipeDismissableNavController()

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = Screens.EXPENSE_GROUP_LIST
        ) {
            composable(Screens.EXPENSE_GROUP_LIST) {
                ExpenseGroupListScreen(
                    navigateToExpenseGroup = {
                        navController.navigate(Screens.EXPENSE_GROUP_DETAIL
                            .replace("{id}", it.toString()))
                    }
                )
            }

            composable(
                Screens.EXPENSE_GROUP_DETAIL,
                arguments = listOf(
                    navArgument("id") {}
                )
            ) {
                ExpenseGroupDetailScreen(id = Uuid.parse(it.arguments?.getString("id")!!))
            }
        }
    }
}