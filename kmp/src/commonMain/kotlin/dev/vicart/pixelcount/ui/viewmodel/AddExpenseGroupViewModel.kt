package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import dev.vicart.pixelcount.model.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class AddExpenseGroupViewModel : ViewModel() {

    val title = MutableStateFlow("")

    val userName = MutableStateFlow("")

    val participants = MutableStateFlow(emptyList<Participant>())

    val canAdd = combine(title, userName, participants) { title, userName, participants ->
        title.isNotBlank() && userName.isNotBlank() && participants.all { it.name.isNotBlank() }
    }

    fun addParticipant(name: String) {
        participants.value += listOf(Participant(name))
    }

    fun deleteParticipant(participant: Participant) {
        participants.value -= listOf(participant)
    }
}