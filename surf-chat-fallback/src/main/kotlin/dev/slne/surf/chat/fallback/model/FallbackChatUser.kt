package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.model.ChatUser
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

class FallbackChatUser(
    override val uuid: UUID,
    override val ignoreList: ObjectSet<UUID>,
    override var pmDisabled: Boolean,
    override var soundEnabled: Boolean,
    override var channelInvites: Boolean
) : ChatUser {
    override suspend fun toggleChannelInvites(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isIgnoring(target: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun ignore(target: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun stopIgnoring(target: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun toggleIgnore(target: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun toggleSound(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun acceptInvite(channel: Channel) {
        TODO("Not yet implemented")
    }

    override suspend fun declineInvite(channel: Channel) {
        TODO("Not yet implemented")
    }

    override suspend fun hasOpenPms(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun togglePm(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun moveToChannel(channel: Channel) {
        TODO("Not yet implemented")
    }

    override suspend fun getName(): String {
        TODO("Not yet implemented")
    }
}