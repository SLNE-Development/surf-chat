package dev.slne.surf.chat.fallback

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.fallback.model.entry.FallbackHistoryEntry
import dev.slne.surf.chat.fallback.util.toPlainText
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services
import java.util.UUID

@AutoService(SurfChatApi::class)
class FallbackSurfChatApi : SurfChatApi, Services.Fallback {
    override suspend fun logMessage(
        player: UUID,
        type: MessageType,
        message: Component,
        messageID: UUID,
        server: String
    ) {
        databaseService.insertHistoryEntry(player, FallbackHistoryEntry(
            entryUuid = messageID,
            userUuid = player,
            type = type,
            timestamp = System.currentTimeMillis(),
            message = message.toPlainText(),
            deletedBy = null,
            server = server
        ))
    }

    override fun createChannel(name: String, owner: ChatUser, ownerName: String) {
        channelService.createChannel(name, owner, ownerName)
    }

    override fun deleteChannel(name: String) {
        channelService.deleteChannel(channelService.getChannel(name) ?: return)
    }

    override fun getChannel(name: String): Channel? {
        return channelService.getChannel(name)
    }
}