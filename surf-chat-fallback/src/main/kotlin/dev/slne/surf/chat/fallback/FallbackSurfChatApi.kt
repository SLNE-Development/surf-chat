package dev.slne.surf.chat.fallback

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.service.channelService
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
        channel: Channel?,
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
                channel = channel?.channelName,
                signature = signedMessage?.signature(),
                messageUuid = messageUuid
            )
        )
    }

    override fun getUser(name: String) = userService.findUserByName(name)
    override fun getUser(uuid: UUID) = userService.findUserByUuid(uuid)

    override suspend fun lookupHistory(filter: HistoryFilter) =
        historyService.findHistoryEntry(filter)

    override fun createChannel(name: String, owner: User) =
        channelService.createChannel(name, owner)

    override fun deleteChannel(channel: Channel) = channelService.deleteChannel(channel)
    override fun getChannel(name: String) = channelService.getChannel(name)
    override fun getChannels() = channelService.getChannels()
    override fun invite(
        channel: Channel,
        user: User
    ) = channelService.invite(channel, user)

    override fun uninvite(
        channel: Channel,
        user: User
    ) = channelService.uninvite(channel, user)

    override fun isInvited(
        channel: Channel,
        user: User
    ) = channelService.isInvited(channel, user)

    override fun acceptInvite(
        channel: Channel,
        user: User
    ) = channelService.acceptInvite(channel, user)

    override fun declineInvite(
        channel: Channel,
        user: User
    ) = channelService.declineInvite(channel, user)
}