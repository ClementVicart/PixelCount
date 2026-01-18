package dev.vicart.pixelcount

import android.app.Application
import dev.vicart.pixelcount.data.database.Database
import dev.vicart.pixelcount.data.database.DriverFactory
import dev.vicart.pixelcount.data.database.createDatabase

class PixelCountApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Database.init(createDatabase(DriverFactory(this)))
    }
}