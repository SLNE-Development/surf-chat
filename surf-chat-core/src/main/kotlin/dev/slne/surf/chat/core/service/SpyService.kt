package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.model.ChatUser
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList

interface SpyService {
    fun getChannelSpys(channel: Channel): ObjectList<ChatUser>
    fun getPrivateMessageSpys(user: ChatUser): ObjectList<ChatUser>

    fun addChannelSpy(user: ChatUser, channel: Channel)
    fun removeChannelSpy(user: ChatUser, channel: Channel)

    fun addPrivateMessageSpy(user: ChatUser, target: ChatUser)
    fun removePrivateMessageSpy(user: ChatUser, target: ChatUser)

    fun hasChannelSpies(channel: Channel): Boolean
    fun hasPrivateMessageSpies(user: ChatUser): Boolean

    fun isChannelSpying(user: ChatUser): Boolean
    fun isPrivateMessageSpying(user: ChatUser): Boolean

    fun clearChannelSpys(user: ChatUser)
    fun clearPrivateMessageSpys(user: ChatUser)

    fun handleDisconnect(user: ChatUser)

    companion object {
        val INSTANCE = requiredService<SpyService>()
    }
}

val spyService get() = SpyService.INSTANCE