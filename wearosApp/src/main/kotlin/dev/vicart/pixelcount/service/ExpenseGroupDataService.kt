package dev.vicart.pixelcount.service

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataItemBuffer
import com.google.android.gms.wearable.DataMapItem
import dev.vicart.pixelcount.shared.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.shared.mapper.ExpenseGroupMapper
import dev.vicart.pixelcount.shared.service.ExpenseGroupService
import kotlinx.coroutines.runBlocking

class ExpenseGroupDataService {

    fun processDataEvent(event: DataEventBuffer) {
        event.filter { it.type == DataEvent.TYPE_CHANGED }.forEach {
            processDataItem(it.dataItem)
        }
    }

    fun processDataItem(dataItem: DataItem) {
        when(dataItem.uri.path) {
            "/expense-groups" -> {

                runBlocking {
                    ExpenseGroupService.deleteAll()
                }

                val map = DataMapItem.fromDataItem(dataItem).dataMap
                val groupsMap = map.getDataMapArrayList("groups")

                val expenseGroups = groupsMap?.map(ExpenseGroupMapper::toExpenseGroup)
                    ?: emptyList()

                expenseGroups.forEach {
                    runBlocking {
                        ExpenseGroupService.insert(it)
                    }
                }
            }
        }
    }
}