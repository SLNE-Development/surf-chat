package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChatUser
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.core.service.HistoryService
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services
import java.util.UUID

@AutoService(HistoryService::class)
class FallbackHistoryService : HistoryService, Services.Fallback {
    override suspend fun write(
        user: UUID,
        type: MessageType,
        message: Component,
        messageID: UUID
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getHistory(user: ChatUser): ObjectList<HistoryEntry> {
        TODO("Not yet implemented")
    }

    override fun logCaching(
        message: SignedMessage.Signature,
        messageID: UUID
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteMessage(name: String, messageID: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun clearChat() {
        TODO("Not yet implemented")
    }

    override fun getMessageIds(): ObjectSet<UUID> {
        TODO("Not yet implemented")
    }
}