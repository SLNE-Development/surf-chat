package dev.slne.surf.chat.fallback

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.core.message.MessageData
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
        signedMessage: SignedMessage?
    ) {
        historyService.logMessage(object : MessageData {
            override val message: Component
                get() = message
            override val messageUuid: UUID
                get() = messageUuid
            override val sender: User
                get() = sender
            override val receiver: User?
                get() = receiver
            override val sentAt: Long
                get() = sentAt
            override val server: String
                get() = server
            override val channel: Channel?
                get() = channel
            override val signedMessage: SignedMessage?
                get() = signedMessage
            override val type: MessageType
                get() = type

        })
    }

    override fun getUser(name: String) = userService.getUser(name)
    override fun getUser(uuid: UUID) = userService.getUser(uuid)
    override fun createUser(
        name: String,
        uuid: UUID
    ) = userService.getOfflineUser(uuid, name)

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