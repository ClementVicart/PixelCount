package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.model.ErrorEnum
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.platform.deleteImage
import dev.vicart.pixelcount.platform.import
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseListViewModel : ViewModel() {

    val expenses = ExpenseGroupRepository.expenseGroups.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val errorChannel = Channel<ErrorEnum>()

    fun deleteExpenseGroup(expenseGroup: ExpenseGroup) {
        viewModelScope.launch {
            ExpenseGroupRepository.deleteExpenseGroup(expenseGroup)
            expenseGroup.expenses.forEach {
                deleteImage(it.id)
            }
        }
    }

    fun importGroup() {
        viewModelScope.launch {
            try {
                val group = import(ExpenseGroup.serializer())
                group?.let {
                    ExpenseGroupRepository.insert(it)
                }
            } catch (e: Exception) {
                errorChannel.send(ErrorEnum.IMPORT_FILE_ERROR)
            }
        }
    }
}