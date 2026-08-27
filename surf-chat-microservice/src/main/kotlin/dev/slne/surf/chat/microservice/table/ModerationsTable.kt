package dev.slne.surf.chat.microservice.table

import dev.slne.surf.chat.core.common.aimoderation.ModerationCategory
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationAction
import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.json.json
import dev.slne.surf.database.table.AuditableLongIdTable
import kotlinx.serialization.json.Json

object ModerationsTable : AuditableLongIdTable("chat_moderations") {
    val messageUuid = nativeUuid("message_uuid").index()
    val action = enumerationByName<ModerationClassificationAction>("action", 50)
    val flaggedScores = json<Map<ModerationCategory, Double>>("flagged_scores", Json)
}