package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.model.ExpenseGroup
import dev.vicart.pixelcount.model.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlin.uuid.Uuid

class AddExpenseGroupViewModel(private val initialItem: ExpenseGroup?) : ViewModel() {

    val emoji = MutableStateFlow(initialItem?.emoji ?: "\uD83D\uDE00")

    val title = MutableStateFlow(initialItem?.title ?: "")

    val userName = MutableStateFlow(initialItem?.participants?.first { it.mandatory }?.name ?: "")

    val participants = MutableStateFlow(initialItem?.participants?.filterNot { it.mandatory } ?: emptyList())

    val canAdd = combine(title, userName, participants) { title, userName, participants ->
        title.isNotBlank() && userName.isNotBlank() && participants.all { it.name.isNotBlank() }
    }

    fun addParticipant(name: String) {
        participants.value += listOf(Participant(name = name))
    }

    fun deleteParticipant(participant: Participant) {
        participants.value -= listOf(participant)
    }

    fun saveExpenseGroup() {
        val expenseGroup = ExpenseGroup(
            id = initialItem?.id ?: Uuid.random(),
            emoji = emoji.value,
            title = title.value,
            participants = participants.value +
                    Participant(id = initialItem?.participants?.first { it.mandatory }?.id ?: Uuid.random(),
                        name = userName.value, mandatory = true),
            expenses = emptyList()
        )
        if(initialItem == null) {
            ExpenseGroupRepository.insert(expenseGroup)
        } else {
            ExpenseGroupRepository.update(expenseGroup)
        }
    }
}