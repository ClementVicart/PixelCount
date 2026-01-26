package dev.vicart.pixelcount.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.vicart.pixelcount.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.model.Expense
import dev.vicart.pixelcount.model.Participant
import dev.vicart.pixelcount.model.PaymentTypeEnum
import dev.vicart.pixelcount.platform.deleteImage
import dev.vicart.pixelcount.platform.pickImage
import dev.vicart.pixelcount.platform.readImage
import dev.vicart.pixelcount.platform.writeImage
import dev.vicart.pixelcount.util.prettyPrint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.decodeToImageBitmap
import java.lang.Exception
import kotlin.time.Clock
import kotlin.uuid.Uuid

class AddExpenseViewModel(itemId: Uuid, private val initial: Expense?) : ViewModel() {

    val sharedWith = MutableStateFlow(initial?.sharedWith)

    val paymentType = MutableStateFlow(initial?.type ?: PaymentTypeEnum.PAYMENT)

    val paidBy = MutableStateFlow(initial?.paidBy)

    val title = MutableStateFlow(initial?.label ?: "")

    val amount = MutableStateFlow(initial?.amount?.prettyPrint ?: "")

    val transferTo = MutableStateFlow(initial?.sharedWith?.firstOrNull())

    val tempPicture = MutableStateFlow<ByteArray?>(null)

    val pictureBitmap = tempPicture.mapLatest {
        it ?: initial?.id?.let { readImage(it) }
    }.mapLatest {
        try {
            it?.decodeToImageBitmap()
        } catch (_: Exception) {
            null
        }
    }.flowOn(Dispatchers.Default)

    val canAdd = combine(paymentType, title, amount, transferTo) { paymentType, title, amount, transferTo ->
        if(paymentType == PaymentTypeEnum.PAYMENT || paymentType == PaymentTypeEnum.REFUND)
            title.isNotBlank() && amount.isNotBlank() && amount.toDoubleOrNull() != null
        else
            amount.isNotBlank() && amount.toDoubleOrNull() != null && transferTo != null
    }

    val expenseGroup = ExpenseGroupRepository.getExpenseGroupFromId(itemId)
        .onEach {
            if(paidBy.value == null) {
                paidBy.value = it?.participants?.single { it.mandatory }
            }
            if(sharedWith.value == null) {
                sharedWith.value = it?.participants?.filterNot { paidBy.value == it }
            }
            if(transferTo.value == null) {
                transferTo.value = it?.participants?.filterNot { paidBy.value == it }?.firstOrNull()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun toggleShareParticipant(participant: Participant) {
        if(sharedWith.value?.contains(participant) == true) {
            sharedWith.value = sharedWith.value!! - participant
        } else {
            sharedWith.value = sharedWith.value!! + participant
        }
    }

    fun changePaidBy(participant: Participant) {
        paidBy.value = participant
        sharedWith.value = expenseGroup.value?.participants?.filterNot { paidBy.value == it }
    }

    fun addExpense() {
        val id = initial?.id ?: Uuid.random()
        val expense = Expense(
            id = id,
            label = title.value,
            amount = amount.value.toDouble().let {
                when(paymentType.value) {
                    PaymentTypeEnum.PAYMENT -> it
                    PaymentTypeEnum.REFUND -> -it
                    else -> it
                }
            },
            paidBy = paidBy.value!!,
            sharedWith = if(paymentType.value == PaymentTypeEnum.TRANSFER) listOf(transferTo.value!!) else sharedWith.value!!,
            datetime = Clock.System.now(),
            type = paymentType.value
        )
        viewModelScope.launch {
            tempPicture.value?.let {
                if(it.isNotEmpty()) {
                    writeImage(id, it)
                } else {
                    deleteImage(id)
                }
            }
            if(initial == null) {
                ExpenseGroupRepository.insertExpense(expense)
            } else {
                ExpenseGroupRepository.updateExpense(expense)
            }
        }
    }

    fun deleteExpense() {
        viewModelScope.launch {
            ExpenseGroupRepository.deleteExpense(initial!!)
            deleteImage(initial.id)
        }
    }

    fun launchPickImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val imageBytes = pickImage()
            if(imageBytes != null) {
                tempPicture.value = imageBytes
            }
        }
    }

    fun removePicture() {
        tempPicture.value = byteArrayOf()
    }
}