package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.model.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale
import kotlin.uuid.Uuid

class AddExpenseGroupViewModel(private val initialItem: ExpenseGroup?) : ViewModel() {

    val emoji = MutableStateFlow(initialItem?.emoji ?: "\uD83D\uDE00")

    val title = MutableStateFlow(initialItem?.title ?: "")

    val userName = MutableStateFlow(initialItem?.participants?.first { it.mandatory }?.name ?: "")

    val participants = MutableStateFlow(initialItem?.participants?.filterNot { it.mandatory } ?: emptyList())

    val canAdd = combine(title, userName, participants) { title, userName, participants ->
        title.isNotBlank() && userName.isNotBlank() && participants.all { it.name.isNotBlank() }
    }

    val currency = MutableStateFlow(initialItem?.currency ?: Currency.getInstance(Locale.getDefault()))

    val availableCurrencies = flowOf(Currency.getAvailableCurrencies().toList())
        .mapLatest { it.sortedBy { it.displayName } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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
            expenses = emptyList(),
            currency = currency.value
        )
        viewModelScope.launch {
            if(initialItem == null) {
                ExpenseGroupRepository.insert(expenseGroup)
            } else {
                ExpenseGroupRepository.update(expenseGroup)
            }
        }
    }
}