package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.core.service.util.HistoryEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

interface HistoryService {
    suspend fun write(user: UUID, type: ChatMessageType, message: Component, messageID: UUID)
    suspend fun getHistory(user: ChatUserModel): ObjectList<HistoryEntryModel>

    fun logCaching(message: HistoryEntry, messageID: UUID)
    fun deleteMessage(name: String, messageID: UUID): Boolean
    fun clearChat()
    fun getMessageIds(): ObjectSet<UUID>

    companion object {
        val INSTANCE = requiredService<HistoryService>()
    }
}

val historyService get() = HistoryService.INSTANCE