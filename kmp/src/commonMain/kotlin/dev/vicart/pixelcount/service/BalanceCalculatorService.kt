package dev.vicart.pixelcount.service

import dev.vicart.pixelcount.model.Balance
import dev.vicart.pixelcount.model.ExpenseGroup

class BalanceCalculatorService(private val expenseGroup: ExpenseGroup) {

    fun calculateBalance() : List<Balance> {
        return emptyList()
    }
}