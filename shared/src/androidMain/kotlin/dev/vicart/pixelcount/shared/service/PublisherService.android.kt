package dev.vicart.pixelcount.shared.service

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dev.vicart.pixelcount.shared.mapper.ExpenseGroupMapper
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

actual class PublisherService(context: Context) {

    private val dataClient = Wearable.getDataClient(context)

    private val publishScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun listenForExpenseGroupChanges() {
        publishScope.launch {
            ExpenseGroupService.expenseGroups.collectLatest {
                publishGroups(it)
            }
        }
    }

    suspend fun publishGroup(group: ExpenseGroup) {
        publishGroups(listOf(group))
    }

    suspend fun publishGroups(groups: List<ExpenseGroup>) {
        val request = PutDataMapRequest.create("/expense-groups")

        groups.map { ExpenseGroupMapper.toDataMap(it) }.also {
            request.dataMap.putDataMapArrayList("groups", ArrayList(it))
        }

        try {
            dataClient.putDataItem(request.asPutDataRequest()).await()
        } catch (e: Exception) {
            Log.w(this::class.simpleName, "Unable to publish groups", e)
        }
    }

}