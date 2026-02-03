package dev.vicart.pixelcount.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.concurrent.futures.await
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.wear.remote.interactions.RemoteActivityHelper
import dev.vicart.pixelcount.shared.service.ExpenseGroupService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ExpenseGroupDetailViewModel(app: Application, id: Uuid) : AndroidViewModel(app) {

    val expenseGroup = ExpenseGroupService.getExpenseGroupFromId(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val myExpenses = ExpenseGroupService.myExpenses(expenseGroup)
    val totalExpenses = ExpenseGroupService.totalExpenses(expenseGroup)

    val expenses = ExpenseGroupService.expenses(expenseGroup).map {
        it.flatMap { it.value }
    }

    fun openOnPhone() {
        viewModelScope.launch {
            val remoteActivityHelper = RemoteActivityHelper(application)
            val intent = Intent(Intent.ACTION_VIEW, "pixelcount://app/group/${expenseGroup.value!!.id}".toUri()).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }

            try {
                remoteActivityHelper.startRemoteActivity(intent).await()
            } catch (e: Exception) {

            }
        }
    }
}