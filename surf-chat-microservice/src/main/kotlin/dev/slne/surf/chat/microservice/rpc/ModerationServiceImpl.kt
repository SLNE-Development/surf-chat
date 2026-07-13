package dev.slne.surf.chat.microservice.rpc

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.core.common.rabbit.rpc.ModerationService
import dev.slne.surf.chat.microservice.table.ModerationsTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object ModerationServiceImpl : ModerationService {
    override suspend fun logModeration(
        messageData: MessageData,
        classification: ModerationClassificationResult
    ) = suspendTransaction {
        ModerationsTable.insert {
            it[messageUuid] = messageData.messageUuid
            it[action] = classification.action
            it[flaggedScores] = buildJsonObject {
                classification.flaggedScores.forEach { (key, value) ->
                    put(key.name, value)
                }
            }.toString()
        }
        Unit
    }
}