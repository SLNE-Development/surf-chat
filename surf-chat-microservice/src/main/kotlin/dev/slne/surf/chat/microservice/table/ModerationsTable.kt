package dev.slne.surf.chat.microservice.table

import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationAction
import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable

object ModerationsTable : AuditableLongIdTable("chat_moderations") {
    val messageUuid = nativeUuid("message_uuid").index()
    val action = enumeration<ModerationClassificationAction>("action")
    val flaggedScores = largeText("flagged_scores")
}