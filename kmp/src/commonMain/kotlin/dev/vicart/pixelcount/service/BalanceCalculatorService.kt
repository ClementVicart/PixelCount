package dev.vicart.pixelcount.service

import dev.vicart.pixelcount.model.Balance
import dev.vicart.pixelcount.model.ExpenseGroup
import dev.vicart.pixelcount.model.Participant
import dev.vicart.pixelcount.model.PaymentTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

        val finalBalances = mutableListOf<Balance>()
        val processedBalances = mutableListOf<Balance>()

        balances.forEach { balance ->
            if(balance in processedBalances) return@forEach

            val reciprocal = balances.find { it.from == balance.to && it.to == balance.from }

            if(reciprocal != null) {
                processedBalances.add(balance)
                processedBalances.add(reciprocal)

                val diff = balance.amount - reciprocal.amount
                when {
                    diff > 0 -> finalBalances.add(Balance(balance.from, balance.to, diff))
                    diff < 0 -> finalBalances.add(Balance(reciprocal.from, reciprocal.to, -diff))
                }
            } else {
                if(balance.amount > 0.0) {
                    finalBalances.add(balance)
                } else if(balance.amount < 0.0) {
                    balance.also {
                        finalBalances.add(Balance(it.to, it.from, -it.amount))
                    }
                }
            }
        }

        finalBalances
    }
}