package dev.vicart.pixelcount.data.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.vicart.pixelcount.data.database.PixelCountDatabase
import dev.vicart.pixelcount.data.database.queries.SelectAll
import dev.vicart.pixelcount.data.database.queries.SelectWhereId
import dev.vicart.pixelcount.model.Expense
import dev.vicart.pixelcount.model.ExpenseGroup
import dev.vicart.pixelcount.model.Participant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlin.math.exp
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
                        it.value.filter { it.payer == false }.map { it.id_ }.contains(participant.id)
                    },
                    datetime = it.value.first().datetime!!,
                    type = it.value.first().type!!
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
                    type = it.value.first().type!!
                )
            },
            currency = it.value.first().currency
        )
    }
}

class ExpenseGroupDao(database: PixelCountDatabase) {

    private val queries = database.expenseGroupQueriesQueries

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectAll = queries.selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .mapLatest(selectAllMapper)

    fun insertExpenseGroup(group: ExpenseGroup) {
        queries.transaction {
            queries.insertExpenseGroup(group.id, group.title, group.emoji, group.currency)
            group.participants.forEach {
                queries.insertParticipant(it.id, it.name, it.mandatory, group.id)
            }
        }
    }

    fun getExpenseGroupFromId(id: Uuid) = queries.selectWhereId(id)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .mapLatest(selectWhereIdMapper)
        .mapLatest { it.firstOrNull() }

    fun updateExpenseGroup(group: ExpenseGroup) {
        queries.transaction {
            queries.updateExpenseGroup(group.title, group.emoji, group.currency, group.id)
            val existingParticipants = queries.selectWhereId(group.id).executeAsList().let(selectWhereIdMapper)
                .flatMap(ExpenseGroup::participants)
            val newParticipants = group.participants - existingParticipants.toSet()
            newParticipants.forEach {
                queries.insertParticipant(it.id, it.name, it.mandatory, group.id)
            }
            queries.deleteParticipantIdNotIn(group.participants.map(Participant::id), group.id)
        }
    }

    fun insertExpense(expense: Expense) {
        queries.transaction {
            queries.insertExpense(expense.id, expense.type, expense.label, expense.amount, expense.datetime)
            queries.insertExpenseWithParticipant(expense.id, expense.paidBy.id, true)
            expense.sharedWith.forEach { participant ->
                queries.insertExpenseWithParticipant(expense.id, participant.id, false)
            }
        }
    }

    fun updateExpense(expense: Expense) {
        queries.transaction {
            queries.updateExpense(expense.label, expense.amount, expense.id)
            queries.deleteExpenseWithParticipant(expense.id)
            queries.insertExpenseWithParticipant(expense.id, expense.paidBy.id, true)
            expense.sharedWith.forEach { participant ->
                queries.insertExpenseWithParticipant(expense.id, participant.id, false)
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        queries.transaction {
            queries.deleteExpenseWithParticipant(expense.id)
            queries.deleteExpense(expense.id)
        }
    }

    fun deleteParticipant(participant: Participant) {
        queries.transaction {
            queries.deleteParticipant(participant.id)
        }
    }

    fun deleteExpenseGroup(expenseGroup: ExpenseGroup) {
        queries.transaction {
            expenseGroup.expenses.forEach(::deleteExpense)
            expenseGroup.participants.forEach(::deleteParticipant)
            queries.deleteExpenseGroup(expenseGroup.id)
        }
    }
}