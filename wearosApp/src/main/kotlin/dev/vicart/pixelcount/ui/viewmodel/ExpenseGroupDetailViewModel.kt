package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.shared.data.repository.ExpenseGroupRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ExpenseGroupDetailViewModel(id: Uuid) : ViewModel() {

    val expenseGroup = ExpenseGroupRepository.getExpenseGroupFromId(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}