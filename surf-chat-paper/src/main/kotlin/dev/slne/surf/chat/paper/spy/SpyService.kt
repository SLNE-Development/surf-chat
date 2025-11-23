package dev.slne.surf.chat.paper.spy

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.core.common.netty.packet.serializer.ChatUuid
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Service

@Service
class SpyService {
    val channelsSpies = mutableObject2ObjectMapOf<Channel, ObjectList<ChatUuid>>()
    val privateMessageSpies = mutableObject2ObjectMapOf<ChatUuid, ObjectList<ChatUuid>>()

    fun getChannelSpies(channel: Channel) =
        channelsSpies.get(channel) ?: mutableObjectListOf()

    fun getPrivateMessageSpies(player: ChatUuid) =
        privateMessageSpies.get(player) ?: mutableObjectListOf()

    fun addChannelSpy(
        player: ChatUuid,
        channel: Channel
    ) = channelsSpies.computeIfAbsent(channel) { mutableObjectListOf() }.add(player)

    fun removeChannelSpy(
        player: ChatUuid,
        channel: Channel
    ) = channelsSpies[channel]?.remove(player) ?: false

    fun addPrivateMessageSpy(player: ChatUuid, target: ChatUuid) =
        privateMessageSpies.computeIfAbsent(target) { mutableObjectListOf() }.add(player)

    fun removePrivateMessageSpy(player: ChatUuid, target: ChatUuid) =
        privateMessageSpies[target]?.remove(player) ?: false

    fun hasChannelSpies(channel: Channel) =
        channelsSpies.containsKey(channel) && channelsSpies[channel]?.isNotEmpty() == true

    fun hasPrivateMessageSpies(player: ChatUuid) =
        privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true

    fun isChannelSpying(player: ChatUuid) = channelsSpies.values.any { it.contains(player) }
    fun isPrivateMessageSpying(player: ChatUuid) =
        privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true

    fun clearChannelSpies(player: ChatUuid) {
        channelsSpies.values.forEach { it.remove(player) }
        channelsSpies.keys.removeIf { channelsSpies[it]?.isEmpty() == true }
    }

    fun clearPrivateMessageSpies(player: ChatUuid) {
        privateMessageSpies.values.forEach { it.remove(player) }
        privateMessageSpies.keys.removeIf { privateMessageSpies[it]?.isEmpty() == true }
    }

    fun cleanup(player: ChatUuid) {
        this.clearChannelSpies(player)
        this.clearPrivateMessageSpies(player)
    }
}

val spyService get() = ChatContextHolderImpl.instance.context.getBean<SpyService>()