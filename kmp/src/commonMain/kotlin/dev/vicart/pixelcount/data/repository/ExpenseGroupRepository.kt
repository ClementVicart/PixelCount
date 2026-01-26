package dev.vicart.pixelcount.data.repository

import dev.vicart.pixelcount.data.database.Database
import dev.vicart.pixelcount.model.Expense
import dev.vicart.pixelcount.model.ExpenseGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlin.uuid.Uuid

object ExpenseGroupRepository {

    private val dao by lazy { Database.expenseGroupDao }
    private val shareScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val expenseGroups = dao.selectAll.shareIn(shareScope, SharingStarted.Eagerly, replay = 1)

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
}
