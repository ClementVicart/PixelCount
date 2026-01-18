package dev.vicart.pixelcount.ui.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object Screens {

    object Expense {

        @Serializable
        data object List : NavKey
    }

    @Serializable
    data object AddExpenseGroup : NavKey
}