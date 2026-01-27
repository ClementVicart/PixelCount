package dev.vicart.pixelcount

import android.app.Application
import com.google.android.gms.wearable.Wearable
import dev.vicart.pixelcount.service.ExpenseGroupDataService
import dev.vicart.pixelcount.shared.data.database.Database
import dev.vicart.pixelcount.shared.data.database.DriverFactory
import dev.vicart.pixelcount.shared.data.database.createDatabase

class PixelCountApplication : Application() {

    companion object {
        val expenseGroupDataService = ExpenseGroupDataService()
    }

    override fun onCreate() {
        super.onCreate()

        Database.init(createDatabase(DriverFactory(this)))
    }
}