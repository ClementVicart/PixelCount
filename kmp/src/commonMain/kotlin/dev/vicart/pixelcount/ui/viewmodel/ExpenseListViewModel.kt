package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.model.ErrorEnum
import dev.vicart.pixelcount.platform.deleteImage
import dev.vicart.pixelcount.platform.import
import dev.vicart.pixelcount.platform.readQrCode
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.service.ExpenseGroupService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ExpenseListViewModel : ViewModel() {

    val expenses = ExpenseGroupService.expenseGroups.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val errorChannel = Channel<ErrorEnum>()

    fun deleteExpenseGroup(expenseGroup: ExpenseGroup) {
        viewModelScope.launch {
            ExpenseGroupService.deleteExpenseGroup(expenseGroup)
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
                    ExpenseGroupService.insert(it)
                }
            } catch (e: Exception) {
                errorChannel.send(ErrorEnum.IMPORT_FILE_ERROR)
            }
        }
    }

    fun readQrCodeGroup() {
        viewModelScope.launch {
            val qrCode = readQrCode()
            if(qrCode != null) {
                try {
                    val group = Json.decodeFromString<ExpenseGroup>(qrCode)
                    ExpenseGroupService.insert(group)
                } catch (e: Exception) {
                    errorChannel.send(ErrorEnum.READING_QR_CODE_ERROR)
                }
            }
        }
    }
}