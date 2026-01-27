package dev.vicart.pixelcount.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import dev.vicart.pixelcount.PixelCountApplication
import dev.vicart.pixelcount.ui.App
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    private lateinit var dataClient: DataClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataClient = Wearable.getDataClient(this)

        lifecycleScope.launch {
            dataClient.dataItems.await().forEach { item ->
                PixelCountApplication.expenseGroupDataService.processDataItem(item)
            }
        }

        setContent {
            App()
        }
    }
}