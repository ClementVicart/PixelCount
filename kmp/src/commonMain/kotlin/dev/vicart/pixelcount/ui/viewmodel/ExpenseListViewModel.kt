package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ExpenseListViewModel : ViewModel() {

    val expenses = ExpenseGroupRepository.expenseGroups.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}