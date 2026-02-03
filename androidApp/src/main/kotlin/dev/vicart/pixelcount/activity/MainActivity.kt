package dev.vicart.pixelcount.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.util.Consumer
import dev.vicart.pixelcount.platform.registerContentExportFactory
import dev.vicart.pixelcount.platform.registerContentProviderFactory
import dev.vicart.pixelcount.shared.service.PublisherService
import dev.vicart.pixelcount.ui.App
import dev.vicart.pixelcount.ui.navigation.LocalDeeplinkUriPath

class MainActivity : ComponentActivity() {

    init {
        registerContentProviderFactory()
        registerContentExportFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        PublisherService(this).listenForExpenseGroupChanges()

        val deepLink = intent.data?.path

        setContent {
            var deepLink by remember { mutableStateOf(deepLink) }
            DisposableEffect(Unit) {
                val listener = Consumer { intent: Intent ->
                    deepLink = intent.data?.path
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }
            CompositionLocalProvider(
                LocalDeeplinkUriPath provides deepLink
            ) {
                App()
            }
        }
    }
}