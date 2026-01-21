package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.model.Expense
import dev.vicart.pixelcount.service.BalanceCalculatorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import kotlin.uuid.Uuid

class ExpenseDetailViewModel(itemId: Uuid) : ViewModel() {

    val expenseGroup = ExpenseGroupRepository.getExpenseGroupFromId(itemId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val myExpenses = expenseGroup.mapLatest {
        (it?.expenses
            ?.filter { it.paidBy.mandatory }
            ?.sumOf(Expense::amount) ?: 0.0)
    }

    val totalExpenses = expenseGroup.mapLatest {
        (it?.expenses
            ?.sumOf(Expense::amount) ?: 0.0)
    }

    val balances = expenseGroup.filterNotNull().mapLatest(::BalanceCalculatorService)
        .mapLatest(BalanceCalculatorService::calculateBalance)
}