package dev.vicart.pixelcount.service

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import dev.vicart.pixelcount.PixelCountApplication

class DataListenerService : WearableListenerService() {

    override fun onDataChanged(event: DataEventBuffer) {
        PixelCountApplication.expenseGroupDataService.processDataEvent(event)
    }
}