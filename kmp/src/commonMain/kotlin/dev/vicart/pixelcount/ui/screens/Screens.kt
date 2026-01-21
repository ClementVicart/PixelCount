package dev.vicart.pixelcount.ui.screens

import androidx.navigation3.runtime.NavKey
import dev.vicart.pixelcount.model.ExpenseGroup
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

object Screens {

    object Expense {

        @Serializable
        data object List : NavKey

        @Serializable
        data class Detail(val id: Uuid) : NavKey
    }

    @Serializable
    data class AddExpenseGroup(val item: ExpenseGroup? = null) : NavKey

    @Serializable
    data class AddExpense(val itemId: Uuid, val initial: dev.vicart.pixelcount.model.Expense? = null) : NavKey
}