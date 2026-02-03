package dev.vicart.pixelcount.shared.data.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.vicart.pixelcount.data.database.queries.SelectAll
import dev.vicart.pixelcount.data.database.queries.SelectWhereId
import dev.vicart.pixelcount.shared.data.database.PixelCountDatabase
import dev.vicart.pixelcount.shared.model.Expense
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.model.Participant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

private val selectAllMapper = { lst: List<SelectAll> ->
    lst.groupBy { it.id }.map {

        val participants = it.value.groupBy { it.id_ }.map {
            Participant(
                id = it.key,
                name = it.value.first().name,
                mandatory = it.value.first().mandatory
            )
        }

        ExpenseGroup(
            id = it.key,
            title = it.value.first().title,
            emoji = it.value.first().emoji,
            participants = participants,
            expenses = it.value.groupBy { it.id__ }.filterNot { it.key == null }.map {
                Expense(
                    id = it.key!!,
                    label = it.value.first().label!!,
                    amount = it.value.first().amount!!,
                    paidBy = participants.single { participant ->
                        it.value.first { it.payer == true }.id_ == participant.id
                    },
                    sharedWith = participants.filter { participant ->
                        it.value.filter { it.payer == false }.map { it.id_ }
                            .contains(participant.id)
                    },
                    datetime = it.value.first().datetime!!,
                    type = it.value.first().type!!,
                    emoji = it.value.first().emoji
                )
            },
            currency = it.value.first().currency
        )
    }
}

private val selectWhereIdMapper = { lst: List<SelectWhereId> ->
    lst.groupBy { it.id }.map {

        val participants = it.value.groupBy { it.id_ }.map {
            Participant(
                id = it.key,
                name = it.value.first().name,
                mandatory = it.value.first().mandatory
            )
        }

        ExpenseGroup(
            id = it.key,
            title = it.value.first().title,
            emoji = it.value.first().emoji,
            participants = participants,
            expenses = it.value.groupBy { it.id__ }.filterNot { it.key == null }.map {
                Expense(
                    id = it.key!!,
                    label = it.value.first().label!!,
                    amount = it.value.first().amount!!,
                    paidBy = participants.single { participant ->
                        it.value.first { it.payer == true }.id_ == participant.id
                    },
                    sharedWith = participants.filter { participant ->
                        it.value.filter { it.payer == false }.map { it.id_ }.contains(participant.id)
                    },
                    datetime = it.value.first().datetime!!,
                    type = it.value.first().type!!,
                    emoji = it.value.first().emoji_!!
                )
            },
            currency = it.value.first().currency
        )
    }
}

class ExpenseGroupDao(database: PixelCountDatabase) {

    private val queries = database.expenseGroupQueriesQueries

    private val shareScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectAll = queries.transactionWithResult {
        queries.selectAll()
    }.asFlow()
        .mapToList(Dispatchers.IO)
        .mapLatest(selectAllMapper)
        .shareIn(shareScope, SharingStarted.Eagerly, 1)

    suspend fun insertExpenseGroup(group: ExpenseGroup) = withContext(Dispatchers.IO) {
        queries.transaction {
            queries.insertExpenseGroup(group.id, group.title, group.emoji, group.currency)
            group.participants.forEach {
                queries.insertParticipant(it.id, it.name, it.mandatory, group.id)
            }
            group.expenses.forEach { expense ->
                launch {
                    insertExpense(expense)
                }
            }
        }
    }

    fun getExpenseGroupFromId(id: Uuid) = queries.transactionWithResult {
        queries.selectWhereId(id)
    }.asFlow()
        .mapToList(Dispatchers.IO)
        .mapLatest(selectWhereIdMapper)
        .mapLatest { it.firstOrNull() }

    suspend fun updateExpenseGroup(group: ExpenseGroup) = withContext(Dispatchers.IO) {
        queries.transaction {
            queries.updateExpenseGroup(group.title, group.emoji, group.currency, group.id)
            val existingGroup = queries.selectWhereId(group.id).executeAsList().let(selectWhereIdMapper)
            val existingParticipants = existingGroup.flatMap(ExpenseGroup::participants)
            group.participants.filter { existingParticipants.map(Participant::id).contains(it.id) }.forEach { participant ->
                queries.updateParticipant(participant.name, participant.id)
            }
            val newParticipants = group.participants.filterNot { existingParticipants.map(Participant::id).contains(it.id) }
            newParticipants.forEach {
                queries.insertParticipant(it.id, it.name, it.mandatory, group.id)
            }
            val deletedParticipants = existingParticipants.filterNot { group.participants.map(Participant::id).contains(it.id) }
            deletedParticipants.forEach { participant ->
                val paidExpenses = existingGroup.flatMap(ExpenseGroup::expenses).filter { it.paidBy == participant }
                launch {
                    paidExpenses.forEach { expense ->
                        deleteExpense(expense)
                    }
                    queries.deleteExpenseWithParticipantByParticipantId(participant.id)
                    deleteParticipant(participant)
                }
            }
        }
    }

    suspend fun insertExpense(expense: Expense) = withContext(Dispatchers.IO) {
        queries.transaction {
            queries.insertExpense(expense.id, expense.type, expense.label, expense.amount, expense.datetime, expense.emoji)
            queries.insertExpenseWithParticipant(expense.id, expense.paidBy.id, true)
            expense.sharedWith.forEach { participant ->
                queries.insertExpenseWithParticipant(expense.id, participant.id, false)
            }
        }
    }

    suspend fun updateExpense(expense: Expense) = withContext(Dispatchers.IO) {
        queries.transaction {
            queries.updateExpense(expense.label, expense.amount, expense.emoji, expense.id)
            queries.deleteExpenseWithParticipantByExpenseId(expense.id)
            queries.insertExpenseWithParticipant(expense.id, expense.paidBy.id, true)
            expense.sharedWith.forEach { participant ->
                queries.insertExpenseWithParticipant(expense.id, participant.id, false)
            }
        }
    }

    suspend fun deleteExpense(expense: Expense) = withContext(Dispatchers.IO) {
        queries.transaction {
            queries.deleteExpenseWithParticipantByExpenseId(expense.id)
            queries.deleteExpense(expense.id)
        }
    }

    suspend fun deleteParticipant(participant: Participant) = withContext(Dispatchers.IO) {
        queries.transaction {
            queries.deleteParticipant(participant.id)
        }
    }

    suspend fun deleteExpenseGroup(expenseGroup: ExpenseGroup) = withContext(Dispatchers.IO) {
        queries.transaction {
            launch {
                expenseGroup.expenses.forEach { deleteExpense(it) }
                expenseGroup.participants.forEach { deleteParticipant(it) }
                queries.deleteExpenseGroup(expenseGroup.id)
            }
        }
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        queries.transaction {
            val allExpenses = queries.selectAll().executeAsList().let(selectAllMapper)
            launch {
                allExpenses.forEach {
                    deleteExpenseGroup(it)
                }
            }
        }
    }
}