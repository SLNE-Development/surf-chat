package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

interface HistoryService {
    suspend fun write(user: UUID, type: MessageType, message: Component, messageID: UUID, server: String)

    fun logCaching(message: SignedMessage.Signature, messageID: UUID)
    suspend fun deleteMessage(name: String, messageID: UUID): Boolean
    fun clearChat()

    companion object {
        val INSTANCE = requiredService<HistoryService>()
    }
}

val historyService get() = HistoryService.INSTANCE