package dev.vicart.pixelcount.shared.service

import dev.vicart.pixelcount.shared.data.database.Database
import dev.vicart.pixelcount.shared.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.shared.model.Expense
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.model.PaymentTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlin.uuid.Uuid

object ExpenseGroupService {

    private val repository = ExpenseGroupRepository(Database.expenseGroupDao)

    var publishService: PublisherService? = null

    private val shareScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val expenseGroups = repository.expenseGroups
        .shareIn(shareScope, SharingStarted.Eagerly, replay = 1)
        .onEach {
            publishService?.publishGroups(it)
        }

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

    suspend fun insert(group: ExpenseGroup) {
        repository.insert(group)
        publishService?.publishGroup(group)
    }

    fun getExpenseGroupFromId(id: Uuid) = repository.getExpenseGroupFromId(id)

    suspend fun update(expenseGroup: ExpenseGroup) {
        repository.update(expenseGroup)
        publishService?.publishGroup(expenseGroup)
    }

    suspend fun insertExpense(group: ExpenseGroup, expense: Expense) {
        repository.insertExpense(expense)
        publishService?.publishGroup(group)
    }

    suspend fun updateExpense(group: ExpenseGroup, expense: Expense) {
        repository.updateExpense(expense)
        publishService?.publishGroup(group)
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