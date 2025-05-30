package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.core.service.SpyService
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services

@AutoService(SpyService::class)
class FallbackSpyService : SpyService, Services.Fallback {
    val channelSpies = mutableMapOf<Channel, ObjectList<ChatUser>>()
    val privateMessageSpies = mutableMapOf<ChatUser, ObjectList<ChatUser>>()

    override fun getChannelSpys(channel: Channel): ObjectList<ChatUser> {
        return channelSpies.getOrDefault(channel, ObjectList.of())
    }

    override fun getPrivateMessageSpys(user: ChatUser): ObjectList<ChatUser> {
        return privateMessageSpies.getOrDefault(user, ObjectList.of())
    }

    override fun addChannelSpy(
        user: ChatUser,
        channel: Channel
    ) {
        channelSpies.computeIfAbsent(channel) { ObjectList.of() }.add(user)
    }

    override fun removeChannelSpy(
        user: ChatUser,
        channel: Channel
    ) {
        channelSpies[channel]?.remove(user)

        if (channelSpies[channel]?.isEmpty() == true) {
            channelSpies.remove(channel)
        }
    }

    override fun addPrivateMessageSpy(
        user: ChatUser,
        target: ChatUser
    ) {
        privateMessageSpies.computeIfAbsent(user) { ObjectList.of() }.add(target)
    }

    override fun removePrivateMessageSpy(
        user: ChatUser,
        target: ChatUser
    ) {
        privateMessageSpies[user]?.remove(target)

        if (privateMessageSpies[user]?.isEmpty() == true) {
            privateMessageSpies.remove(user)
        }
    }

    override fun hasChannelSpies(channel: Channel): Boolean {
        return channelSpies.containsKey(channel) && channelSpies[channel]?.isNotEmpty() == true
    }

    override fun hasPrivateMessageSpies(user: ChatUser): Boolean {
        return privateMessageSpies.containsKey(user) && privateMessageSpies[user]?.isNotEmpty() == true
    }

    override fun isChannelSpying(user: ChatUser): Boolean {
        return channelSpies.values.any { it.contains(user) }
    }

    override fun isPrivateMessageSpying(user: ChatUser): Boolean {
        return privateMessageSpies.values.any { it.contains(user) }
    }

    override fun clearChannelSpys(user: ChatUser) {
        channelSpies.values.forEach { it.remove(user) }
        channelSpies.entries.removeIf { it.value.isEmpty() }
    }

    override fun clearPrivateMessageSpys(user: ChatUser) {
        privateMessageSpies.remove(user)
    }

    override fun handleDisconnect(user: ChatUser) {
        this.clearChannelSpys(user)
        this.clearPrivateMessageSpys(user)
    }
}