package dev.vicart.pixelcount.shared.service

import dev.vicart.pixelcount.shared.model.ExpenseGroup

expect class PublisherService {

    suspend fun publishGroup(group: ExpenseGroup)

    suspend fun publishGroups(groups: List<ExpenseGroup>)
}