package dev.vicart.pixelcount.shared.mapper

import com.google.android.gms.wearable.DataMap
import dev.vicart.pixelcount.shared.model.Expense
import dev.vicart.pixelcount.shared.model.ExpenseGroup
import dev.vicart.pixelcount.shared.model.Participant
import dev.vicart.pixelcount.shared.model.PaymentTypeEnum
import kotlinx.serialization.json.Json
import java.util.Currency
import kotlin.time.Instant
import kotlin.uuid.Uuid

object ExpenseGroupMapper {

    fun toDataMap(expenseGroup: ExpenseGroup) : DataMap {
        return DataMap().apply {
            putString("id", expenseGroup.id.toString())
            putString("emoji", expenseGroup.emoji)
            putString("title", expenseGroup.title)
            putString("currency", expenseGroup.currency.currencyCode)
            putDataMapArrayList("participants", expenseGroup.participants.map(::toDataMap)
                .let { ArrayList(it) })
            putDataMapArrayList("expenses", expenseGroup.expenses.map(::toDataMap)
                .let { ArrayList(it) })
        }
    }

    private fun toDataMap(participant: Participant) : DataMap {
        return DataMap().apply {
            putString("id", participant.id.toString())
            putString("name", participant.name)
            putBoolean("mandatory", participant.mandatory)
        }
    }

    private fun toDataMap(expense: Expense) : DataMap {
        return DataMap().apply {
            putString("id", expense.id.toString())
            putString("label", expense.label)
            putString("type", expense.type.name)
            putDouble("amount", expense.amount)
            putLong("datetime", expense.datetime.toEpochMilliseconds())
            putDataMap("paidBy", toDataMap(expense.paidBy))
            putDataMapArrayList("sharedWith", expense.sharedWith.map(::toDataMap)
                .let { ArrayList(it) })
        }
    }

    fun toExpenseGroup(dataMap: DataMap) : ExpenseGroup {
        return ExpenseGroup(
            id = Uuid.parse(dataMap.getString("id")!!),
            title = dataMap.getString("title")!!,
            emoji = dataMap.getString("emoji")!!,
            currency = Currency.getInstance(dataMap.getString("currency")!!),
            participants = dataMap.getDataMapArrayList("participants")!!.map(::toParticipant),
            expenses = dataMap.getDataMapArrayList("expenses")?.map {
                Expense(
                    id = Uuid.parse(it.getString("id")!!),
                    label = it.getString("label")!!,
                    type = PaymentTypeEnum.valueOf(it.getString("type")!!),
                    amount = it.getDouble("amount"),
                    datetime = Instant.fromEpochMilliseconds(it.getLong("datetime")),
                    paidBy = toParticipant(it.getDataMap("paidBy")!!),
                    sharedWith = it.getDataMapArrayList("sharedWith")?.map(::toParticipant)
                        ?: emptyList(),
                    emoji = it.getString("emoji").orEmpty()
                )
            } ?: emptyList()
        )
    }

    fun toParticipant(dataMap: DataMap) : Participant {
        return Participant(
            id = Uuid.parse(dataMap.getString("id")!!),
            name = dataMap.getString("name")!!,
            mandatory = dataMap.getBoolean("mandatory")
        )
    }
}