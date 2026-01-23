package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.model.Expense
import dev.vicart.pixelcount.model.Participant
import dev.vicart.pixelcount.model.PaymentTypeEnum
import dev.vicart.pixelcount.util.prettyPrint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.math.exp
import kotlin.time.Clock
import kotlin.uuid.Uuid

class AddExpenseViewModel(itemId: Uuid, private val initial: Expense?) : ViewModel() {

    val sharedWith = MutableStateFlow(initial?.sharedWith)

    val paymentType = MutableStateFlow(initial?.type ?: PaymentTypeEnum.PAYMENT)

    val paidBy = MutableStateFlow(initial?.paidBy)

    val title = MutableStateFlow(initial?.label ?: "")

    val amount = MutableStateFlow(initial?.amount?.prettyPrint ?: "")

    val transferTo = MutableStateFlow(initial?.sharedWith?.firstOrNull())

    val canAdd = combine(paymentType, title, amount, transferTo) { paymentType, title, amount, transferTo ->
        if(paymentType == PaymentTypeEnum.PAYMENT || paymentType == PaymentTypeEnum.REFUND)
            title.isNotBlank() && amount.isNotBlank() && amount.toDoubleOrNull() != null &&
                    amount.toDouble() <= 1000.0
        else
            amount.isNotBlank() && amount.toDoubleOrNull() != null && amount.toDouble() <= 1000.0
    }

    val expenseGroup = ExpenseGroupRepository.getExpenseGroupFromId(itemId)
        .onEach {
            if(paidBy.value == null) {
                paidBy.value = it?.participants?.single { it.mandatory }
            }
            if(sharedWith.value == null) {
                sharedWith.value = it?.participants?.filterNot { paidBy.value == it }
            }
            if(transferTo.value == null) {
                transferTo.value = it?.participants?.filterNot { paidBy.value == it }?.firstOrNull()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun toggleShareParticipant(participant: Participant) {
        if(sharedWith.value?.contains(participant) == true) {
            sharedWith.value = sharedWith.value!! - participant
        } else {
            sharedWith.value = sharedWith.value!! + participant
        }
    }

    fun changePaidBy(participant: Participant) {
        paidBy.value = participant
        sharedWith.value = expenseGroup.value?.participants?.filterNot { paidBy.value == it }
    }

    fun addExpense() {
        val expense = Expense(
            id = initial?.id ?: Uuid.random(),
            label = title.value,
            amount = amount.value.toDouble().let {
                when(paymentType.value) {
                    PaymentTypeEnum.PAYMENT -> it
                    PaymentTypeEnum.REFUND -> -it
                    else -> it
                }
            },
            paidBy = paidBy.value!!,
            sharedWith = if(paymentType.value == PaymentTypeEnum.TRANSFER) listOf(transferTo.value!!) else sharedWith.value!!,
            datetime = Clock.System.now(),
            type = paymentType.value
        )
        if(initial == null) {
            ExpenseGroupRepository.insertExpense(expense)
        } else {
            ExpenseGroupRepository.updateExpense(expense)
        }
    }

    fun deleteExpense() {
        ExpenseGroupRepository.deleteExpense(initial!!)
    }
}