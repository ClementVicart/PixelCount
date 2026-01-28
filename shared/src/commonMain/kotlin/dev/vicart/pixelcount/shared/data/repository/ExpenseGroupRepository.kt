package dev.vicart.pixelcount.shared.data.repository

import dev.vicart.pixelcount.shared.data.dao.ExpenseGroupDao
import dev.vicart.pixelcount.shared.data.database.Database
import dev.vicart.pixelcount.shared.model.Expense
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.service.PublisherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlin.uuid.Uuid

class ExpenseGroupRepository(private val dao: ExpenseGroupDao) {

    val expenseGroups = dao.selectAll

    suspend fun insert(expenseGroup: ExpenseGroup) {
        dao.insertExpenseGroup(expenseGroup)
    }

    fun getExpenseGroupFromId(id: Uuid) = dao.getExpenseGroupFromId(id)

    suspend fun update(expenseGroup: ExpenseGroup) = dao.updateExpenseGroup(expenseGroup)

    suspend fun insertExpense(expense: Expense) {
        dao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        dao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense)
    }

    suspend fun deleteExpenseGroup(expenseGroup: ExpenseGroup) {
        dao.deleteExpenseGroup(expenseGroup)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
