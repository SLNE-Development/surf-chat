package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.core.service.SpyService
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services

@AutoService(SpyService::class)
class FallbackSpyService : SpyService, Services.Fallback {
    override fun getChannelSpys(channel: Channel): ObjectList<ChatUser> {
        TODO("Not yet implemented")
    }

    override fun getPrivateMessageSpys(user: ChatUser): ObjectList<ChatUser> {
        TODO("Not yet implemented")
    }

    override fun addChannelSpy(
        user: ChatUser,
        channel: Channel
    ) {
        TODO("Not yet implemented")
    }

    override fun removeChannelSpy(
        user: ChatUser,
        channel: Channel
    ) {
        TODO("Not yet implemented")
    }

    override fun addPrivateMessageSpy(
        user: ChatUser,
        target: ChatUser
    ) {
        TODO("Not yet implemented")
    }

    override fun removePrivateMessageSpy(
        user: ChatUser,
        target: ChatUser
    ) {
        TODO("Not yet implemented")
    }

    override fun hasChannelSpies(channel: Channel): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasPrivateMessageSpies(user: ChatUser): Boolean {
        TODO("Not yet implemented")
    }

    override fun isChannelSpying(user: ChatUser): Boolean {
        TODO("Not yet implemented")
    }

    override fun isPrivateMessageSpying(user: ChatUser): Boolean {
        TODO("Not yet implemented")
    }

    override fun clearChannelSpys(user: ChatUser) {
        TODO("Not yet implemented")
    }

    override fun clearPrivateMessageSpys(user: ChatUser) {
        TODO("Not yet implemented")
    }

    override fun handleDisconnect(user: ChatUser) {
        TODO("Not yet implemented")
    }
}