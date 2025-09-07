package dev.slne.surf.chat.fallback.table

import dev.slne.surf.chat.api.entry.DenylistActionType
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.jetbrains.exposed.dao.id.IntIdTable

object DenylistActionsTable : IntIdTable("chat_denylist_actions") {
    var name = varchar("name", 64).uniqueIndex()
    var actionType =
        varchar("action_type", 16).transform({ DenylistActionType.valueOf(it) }, { it.toString() })
    var reason = largeText("reason").transform(
        { GsonComponentSerializer.gson().deserialize(it) },
        { GsonComponentSerializer.gson().serialize(it) })
}