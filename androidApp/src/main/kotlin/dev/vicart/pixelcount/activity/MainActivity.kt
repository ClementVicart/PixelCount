package dev.vicart.pixelcount.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.vicart.pixelcount.platform.registerContentExportFactory
import dev.vicart.pixelcount.platform.registerContentProviderFactory
import dev.vicart.pixelcount.shared.service.PublisherService
import dev.vicart.pixelcount.ui.App

class MainActivity : ComponentActivity() {

    init {
        registerContentProviderFactory()
        registerContentExportFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        PublisherService(this).listenForExpenseGroupChanges()

        setContent {
            App()
        }
    }
}