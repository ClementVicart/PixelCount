package dev.vicart.pixelcount.service

import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.WearableListenerService
import dev.vicart.pixelcount.shared.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.shared.mapper.ExpenseGroupMapper
import kotlinx.coroutines.runBlocking

class DataListenerService : WearableListenerService() {

    override fun onDataChanged(event: DataEventBuffer) {
        event.filter { it.type == DataEvent.TYPE_CHANGED }.forEach {
            when(it.dataItem.uri.path) {
                "/expense-groups" -> {

                    runBlocking {
                        ExpenseGroupRepository.deleteAll()
                    }

                    val map = DataMapItem.fromDataItem(it.dataItem).dataMap
                    val groupsMap = map.getDataMapArrayList("groups")

                    val expenseGroups = groupsMap?.map(ExpenseGroupMapper::toExpenseGroup)
                        ?: emptyList()

                    expenseGroups.forEach {
                        runBlocking {
                            ExpenseGroupRepository.insert(it)
                        }
                    }
                }
            }
        }
    }
}