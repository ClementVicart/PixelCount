package dev.vicart.pixelcount.service

import dev.vicart.pixelcount.shared.model.Balance
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.model.Participant
import dev.vicart.pixelcount.shared.model.PaymentTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.collections.forEach

class BalanceCalculatorService(private val expenseGroup: ExpenseGroup) {

    suspend fun calculateBalance() : List<Balance> = withContext(Dispatchers.Default) {
        val totalAmountDuePerParticipant = expenseGroup.expenses.filter { it.type != PaymentTypeEnum.TRANSFER }.groupBy { it.paidBy }.mapValues { entry ->
            entry.value.flatMap { expense ->
                expense.sharedWith.map {
                    it to (expense.amount / (expense.sharedWith.size+1))
                }
            }.fold(mutableMapOf<Participant, Double>()) { acc, pair ->
                acc[pair.first] = acc.getOrPut(pair.first) { 0.0 } + pair.second
                acc
            }.mapValues { (debitor, amount) ->
                val transfers = expenseGroup.expenses
                    .filter { it.type == PaymentTypeEnum.TRANSFER && it.paidBy == debitor && it.sharedWith.contains(entry.key) }

                amount - transfers.sumOf { it.amount }
            }
        }

        val balances = totalAmountDuePerParticipant.flatMap { entry ->
            entry.value.map { value ->
                Balance(value.key, entry.key, value.value)
            }
        }

        eliminateTransitiveBalances(balances)
    }

    private fun eliminateTransitiveBalances(balances: List<Balance>): List<Balance> {
        val result = mutableListOf<Balance>()

        val participantsNet = buildMap {
            balances.forEach { balance ->
                put(balance.from, getOrElse(balance.from) { 0.0 } - balance.amount)
                put(balance.to, getOrElse(balance.to) { 0.0 } + balance.amount)
            }
        }

        val debtors = participantsNet.filter { it.value < 0.0 }.map { it.key to -it.value }.toMutableList()
        val creditors = participantsNet.filter { it.value > 0.0 }.map { it.key to it.value }.toMutableList()

        while(debtors.isNotEmpty() && creditors.isNotEmpty()) {
            val debitor = debtors.removeAt(0)
            val creditor = creditors.removeAt(0)

            val transferAmount = minOf(debitor.second, creditor.second)

            result.add(Balance(debitor.first, creditor.first, transferAmount))

            if(debitor.second - transferAmount > 0.0) {
                debtors.add(debitor.first to debitor.second - transferAmount)
            }
            if(creditor.second - transferAmount > 0.0) {
                creditors.add(creditor.first to creditor.second - transferAmount)
            }
        }

        return result
    }
}
