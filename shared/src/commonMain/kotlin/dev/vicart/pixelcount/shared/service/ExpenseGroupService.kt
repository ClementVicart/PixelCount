package dev.vicart.pixelcount.shared.service

import dev.vicart.pixelcount.shared.data.database.Database
import dev.vicart.pixelcount.shared.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.shared.model.Expense
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.model.PaymentTypeEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.toSortedMap
import kotlin.uuid.Uuid

object ExpenseGroupService {

    private val repository = ExpenseGroupRepository(Database.expenseGroupDao)

    val expenseGroups = repository.expenseGroups

    fun myExpenses(expenseGroupFlow: Flow<ExpenseGroup?>) : Flow<Double> = expenseGroupFlow.mapLatest {
        (it?.expenses
            ?.filter { it.paidBy.mandatory }
            ?.filter { it.type == PaymentTypeEnum.PAYMENT }
            ?.sumOf(Expense::amount) ?: 0.0)
    }

    fun totalExpenses(expenseGroupFlow: Flow<ExpenseGroup?>) : Flow<Double> = expenseGroupFlow.mapLatest {
        (it?.expenses
            ?.filter { it.type == PaymentTypeEnum.PAYMENT }
            ?.sumOf(Expense::amount) ?: 0.0)
    }

    fun expenses(expenseGroupFlow: Flow<ExpenseGroup?>) = expenseGroupFlow.filterNotNull().mapLatest { it.expenses }
        .mapLatest {
            it.groupBy { it.datetime.toLocalDateTime(TimeZone.currentSystemDefault()).date }.mapValues {
                it.value.sortedByDescending { it.datetime }
            }.toSortedMap { first, second -> second.compareTo(first) }
        }

    suspend fun insert(group: ExpenseGroup) {
        repository.insert(group)
    }

    fun getExpenseGroupFromId(id: Uuid) = repository.getExpenseGroupFromId(id)

    suspend fun update(expenseGroup: ExpenseGroup) {
        repository.update(expenseGroup)
    }

    suspend fun insertExpense(expense: Expense) {
        repository.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        repository.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        repository.deleteExpense(expense)
    }

    suspend fun deleteExpenseGroup(expenseGroup: ExpenseGroup) {
        repository.deleteExpenseGroup(expenseGroup)
    }

    suspend fun deleteAll() {
        repository.deleteAll()
    }
}