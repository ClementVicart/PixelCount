package dev.vicart.pixelcount

import android.app.Application
import dev.vicart.pixelcount.shared.data.database.Database
import dev.vicart.pixelcount.shared.data.database.DriverFactory
import dev.vicart.pixelcount.shared.data.database.createDatabase
import dev.vicart.pixelcount.shared.data.repository.ExpenseGroupRepository
import dev.vicart.pixelcount.shared.service.PublisherService

class PixelCountApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Database.init(createDatabase(DriverFactory(this)))
        ExpenseGroupRepository.publishService = PublisherService(this)
    }
}