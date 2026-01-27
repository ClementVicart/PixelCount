package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.shared.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.shared.service.ExpenseGroupService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ExpenseGroupListViewModel : ViewModel() {

    val expenseGroups = ExpenseGroupService.expenseGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}