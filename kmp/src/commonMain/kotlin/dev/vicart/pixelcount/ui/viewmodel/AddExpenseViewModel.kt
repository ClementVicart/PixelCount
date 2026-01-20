package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.model.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.uuid.Uuid

class AddExpenseViewModel(private val itemId: Uuid) : ViewModel() {

    val sharedWith = MutableStateFlow<List<Participant>?>(null)

    val paidBy = MutableStateFlow<Participant?>(null)

    val title = MutableStateFlow("")

    val amount = MutableStateFlow("")

    val expenseGroup = ExpenseGroupRepository.getExpenseGroupFromId(itemId)
        .onEach {
            if(paidBy.value == null) {
                paidBy.value = it.participants.single { it.mandatory }
            }
            if(sharedWith.value == null) {
                sharedWith.value = it.participants.filterNot { paidBy.value == it }
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
}