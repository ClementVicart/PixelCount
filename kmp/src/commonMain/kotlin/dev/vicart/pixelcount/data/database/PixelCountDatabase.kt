package dev.vicart.pixelcount.data.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import dev.vicart.pixelcount.data.dao.ExpenseGroupDao
import dev.vicart.pixelcount.model.PaymentTypeEnum
import java.util.Currency
import kotlin.time.Instant
import kotlin.uuid.Uuid

expect class DriverFactory {
    fun createDriver() : SqlDriver
}

private val uuidAdapter = object : ColumnAdapter<Uuid, String> {
    override fun decode(databaseValue: String): Uuid {
        return Uuid.parse(databaseValue)
    }

    override fun encode(value: Uuid): String {
        return value.toString()
    }
}

private val datetimeAdapter = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant {
        return Instant.fromEpochSeconds(databaseValue)
    }

    override fun encode(value: Instant): Long {
        return value.epochSeconds
    }
}

private val currencyAdapter = object : ColumnAdapter<Currency, String> {
    override fun decode(databaseValue: String): Currency {
        return Currency.getInstance(databaseValue)
    }

    override fun encode(value: Currency): String {
        return value.currencyCode
    }

}

private val paymentTypeEnum = object : ColumnAdapter<PaymentTypeEnum, String> {
    override fun decode(databaseValue: String): PaymentTypeEnum {
        return PaymentTypeEnum.valueOf(databaseValue)
    }

    override fun encode(value: PaymentTypeEnum): String {
        return value.name
    }

}

fun createDatabase(driverFactory: DriverFactory) : PixelCountDatabase {
    val driver = driverFactory.createDriver()
    val database = PixelCountDatabase(
        driver = driver,
        ExpenseGroupAdapter = ExpenseGroup.Adapter(
            idAdapter = uuidAdapter,
            currencyAdapter = currencyAdapter
        ),
        ExpenseParticipantAdapter = ExpenseParticipant.Adapter(
            idAdapter = uuidAdapter,
            eg_idAdapter = uuidAdapter
        ),
        ExpenseGroupExpenseAdapter = ExpenseGroupExpense.Adapter(
            idAdapter = uuidAdapter,
            datetimeAdapter = datetimeAdapter,
            typeAdapter = paymentTypeEnum
        ),
        ExpenseGroupExpenseParticipantAdapter = ExpenseGroupExpenseParticipant.Adapter(
            ege_idAdapter = uuidAdapter,
            ep_idAdapter = uuidAdapter
        )
    )

    return database
}

object Database {

    lateinit var expenseGroupDao: ExpenseGroupDao

    lateinit var instance: PixelCountDatabase
        private set

    fun init(db: PixelCountDatabase) {
        instance = db
        expenseGroupDao = ExpenseGroupDao(db)
    }
}