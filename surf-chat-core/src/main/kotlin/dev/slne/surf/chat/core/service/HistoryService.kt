package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.util.history.LoggedMessage
import dev.slne.surf.surfapi.core.api.util.requiredService

import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import java.util.UUID

interface HistoryService {
    suspend fun write(user: UUID, type: ChatMessageType, message: Component)
    suspend fun getHistory(user: ChatUserModel): ObjectList<HistoryEntryModel>

    fun logCaching(player: UUID, message: LoggedMessage, messageID: UUID)
    fun deleteMessage(player: UUID, messageID: UUID)
    fun resendMessages(player: UUID)
    fun clearChat()

    companion object {
        val INSTANCE = requiredService<HistoryService>()
    }
}

val historyService get() = HistoryService.INSTANCE