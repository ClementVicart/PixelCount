package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ExpenseListViewModel : ViewModel() {

    val expenses = MutableStateFlow(emptyList<Any>())
}