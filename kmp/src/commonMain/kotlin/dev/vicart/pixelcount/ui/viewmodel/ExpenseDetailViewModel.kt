package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.model.Balance
import dev.vicart.pixelcount.model.Expense
import dev.vicart.pixelcount.model.ExpenseGroup
import dev.vicart.pixelcount.model.PaymentTypeEnum
import dev.vicart.pixelcount.platform.deleteImage
import dev.vicart.pixelcount.platform.export
import dev.vicart.pixelcount.platform.hasImage
import dev.vicart.pixelcount.service.BalanceCalculatorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.math.RoundingMode
import kotlin.collections.toSortedMap
import kotlin.time.Clock
import kotlin.uuid.Uuid

class ExpenseDetailViewModel(itemId: Uuid) : ViewModel() {

    val expenseGroup = ExpenseGroupRepository.getExpenseGroupFromId(itemId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val expenses = expenseGroup.filterNotNull().mapLatest { it.expenses }
        .mapLatest {
            it.groupBy { it.datetime.toLocalDateTime(TimeZone.currentSystemDefault()).date }.mapValues {
                it.value.sortedByDescending { it.datetime }
            }.toSortedMap { first, second -> second.compareTo(first) }
        }

    val myExpenses = expenseGroup.mapLatest {
        (it?.expenses
            ?.filter { it.paidBy.mandatory }
            ?.filter { it.type == PaymentTypeEnum.PAYMENT }
            ?.sumOf(Expense::amount) ?: 0.0)
    }

    val totalExpenses = expenseGroup.mapLatest {
        (it?.expenses
            ?.filter { it.type == PaymentTypeEnum.PAYMENT }
            ?.sumOf(Expense::amount) ?: 0.0)
    }

    val balances = expenseGroup.filterNotNull().mapLatest(::BalanceCalculatorService)
        .mapLatest(BalanceCalculatorService::calculateBalance)

    val availableExpenseImage = expenses.mapLatest {
        it.flatMap { it.value }.filter { hasImage(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteExpenseGroup() {
        viewModelScope.launch {
            ExpenseGroupRepository.deleteExpenseGroup(expenseGroup.value!!)
            expenseGroup.value!!.expenses.forEach {
                deleteImage(it.id)
            }
        }
    }

    fun completeTransfer(balance: Balance) {
        val expense = Expense(
            type = PaymentTypeEnum.TRANSFER,
            label = "",
            amount = balance.amount,
            paidBy = balance.from,
            sharedWith = listOf(balance.to),
            datetime = Clock.System.now()
        )

        viewModelScope.launch {
            ExpenseGroupRepository.insertExpense(expense)
        }
    }

    fun exportGroup() {
        export(expenseGroup.value!!.title, expenseGroup.value!!, ExpenseGroup.serializer())
    }
}