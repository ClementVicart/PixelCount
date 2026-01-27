package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.shared.model.Balance
import dev.vicart.pixelcount.shared.model.Expense
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.model.PaymentTypeEnum
import dev.vicart.pixelcount.platform.deleteImage
import dev.vicart.pixelcount.platform.export
import dev.vicart.pixelcount.platform.hasImage
import dev.vicart.pixelcount.service.BalanceCalculatorService
import dev.vicart.pixelcount.shared.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.shared.service.ExpenseGroupService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.toSortedMap
import kotlin.time.Clock
import kotlin.uuid.Uuid

class ExpenseDetailViewModel(itemId: Uuid) : ViewModel() {

    val expenseGroup = ExpenseGroupService.getExpenseGroupFromId(itemId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val myExpenses = ExpenseGroupService.myExpenses(expenseGroup)

    val totalExpenses = ExpenseGroupService.totalExpenses(expenseGroup)

    val expenses = expenseGroup.filterNotNull().mapLatest { it.expenses }
        .mapLatest {
            it.groupBy { it.datetime.toLocalDateTime(TimeZone.currentSystemDefault()).date }.mapValues {
                it.value.sortedByDescending { it.datetime }
            }.toSortedMap { first, second -> second.compareTo(first) }
        }

    val balances = expenseGroup.filterNotNull().mapLatest(::BalanceCalculatorService)
        .mapLatest(BalanceCalculatorService::calculateBalance)

    val availableExpenseImage = expenses.mapLatest {
        it.flatMap { it.value }.filter { hasImage(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteExpenseGroup() {
        viewModelScope.launch {
            ExpenseGroupService.deleteExpenseGroup(expenseGroup.value!!)
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
            ExpenseGroupService.insertExpense(expense)
        }
    }

    fun exportGroup() {
        export(expenseGroup.value!!.title, expenseGroup.value!!, ExpenseGroup.serializer())
    }
}