package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.model.ExpenseGroup
import dev.vicart.pixelcount.platform.deleteImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseListViewModel : ViewModel() {

    val expenses = ExpenseGroupRepository.expenseGroups.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteExpenseGroup(expenseGroup: ExpenseGroup) {
        ExpenseGroupRepository.deleteExpenseGroup(expenseGroup)
        expenseGroup.expenses.forEach {
            viewModelScope.launch {
                deleteImage(it.id)
            }
        }
    }
}