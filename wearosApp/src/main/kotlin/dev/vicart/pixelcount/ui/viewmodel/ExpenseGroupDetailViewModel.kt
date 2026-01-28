package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.shared.service.ExpenseGroupService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ExpenseGroupDetailViewModel(id: Uuid) : ViewModel() {

    val expenseGroup = ExpenseGroupService.getExpenseGroupFromId(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val myExpenses = ExpenseGroupService.myExpenses(expenseGroup)
    val totalExpenses = ExpenseGroupService.totalExpenses(expenseGroup)

    val expenses = ExpenseGroupService.expenses(expenseGroup).map {
        it.flatMap { it.value }
    }
}