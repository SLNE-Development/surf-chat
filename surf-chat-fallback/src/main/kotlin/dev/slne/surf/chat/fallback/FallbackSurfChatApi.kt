package dev.slne.surf.chat.fallback

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.userService
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SurfChatApi::class)
class FallbackSurfChatApi : SurfChatApi, Services.Fallback {
    override suspend fun logMessage(
        message: Component,
        type: MessageType,
        sender: User,
        receiver: User?,
        sentAt: Long,
        server: String,
        signedMessage: SignedMessage?,
        messageUuid: UUID
    ) {
        historyService.logMessage(
            MessageData(
                message = message,
                type = type,
                sender = sender,
                receiver = receiver,
                sentAt = sentAt,
                server = server,
                signature = signedMessage?.signature(),
                messageUuid = messageUuid
            )
        )
    }

    override fun getUser(name: String) = userService.findUserByName(name)
    override fun getUser(uuid: UUID) = userService.findUserByUuid(uuid)

    override suspend fun lookupHistory(filter: HistoryFilter) =
        historyService.findHistoryEntry(filter)
}