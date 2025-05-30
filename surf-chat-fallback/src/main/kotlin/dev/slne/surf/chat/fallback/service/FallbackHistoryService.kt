package dev.slne.surf.chat.fallback.service

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.fallback.model.entry.FallbackHistoryEntry
import dev.slne.surf.chat.fallback.util.toPlainText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
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
        messageID: UUID,
        server: String
    ) {
        databaseService.insertHistoryEntry(user, FallbackHistoryEntry(
            entryUuid = messageID,
            userUuid = user,
            type = type,
            timestamp = System.currentTimeMillis(),
            message.toPlainText(),
            deletedBy = null,
            server = server
        ))
    }

    val localCache = mutableObject2ObjectMapOf<UUID, SignedMessage.Signature>()

    override fun logCaching(
        message: SignedMessage.Signature,
        messageID: UUID
    ) {
        localCache[messageID] = message
    }

    override suspend fun deleteMessage(name: String, messageID: UUID): Boolean {
        val message = localCache[messageID] ?: return false

        //TODO: Implement deletion logic for online players
        databaseService.markMessageDeleted(name, messageID)
        return true
    }

    override fun clearChat() {

    }
}