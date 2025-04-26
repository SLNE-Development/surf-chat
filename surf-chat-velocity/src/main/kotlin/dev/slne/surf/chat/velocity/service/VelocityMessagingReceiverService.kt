package dev.slne.surf.chat.velocity.service

import com.google.common.io.ByteStreams
import com.google.gson.reflect.TypeToken

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier

import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.core.service.messaging.MessagingReceiverService
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import dev.slne.surf.chat.velocity.gson
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.chat.velocity.util.debug
import it.unimi.dsi.fastutil.objects.ObjectSet

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

import java.util.*

class VelocityMessagingReceiverService(): MessagingReceiverService {
    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        if(!event.identifier.equals(IDENTIFIER)) {
            return
        }

        event.result = PluginMessageEvent.ForwardResult.handled()

        if (event.source !is ServerConnection) {
            return
        }

        val input = ByteStreams.newDataInput(event.data)

        val sender = input.readUTF()
        val target = input.readUTF()
        val messageJson = input.readUTF()
        val typeJson = input.readUTF()
        val messageId = UUID.fromString(input.readUTF())
        val chatChannel = input.readUTF()
        val forwardingServers = gson.fromJson<ObjectSet<String>>(
            input.readUTF(),
            object : TypeToken<ObjectSet<String>>() {}.type
        )

        val chatMessage = GsonComponentSerializer.gson().deserialize(messageJson)
        val messageType = gson.fromJson(typeJson, ChatMessageType::class.java)

        handleReceive (
            player = sender,
            target = target,
            message = chatMessage,
            type = messageType,
            messageID = messageId,
            channel = chatChannel,
            forwardingServers
        )
    }

    override fun handleReceive(
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: UUID,
        channel: String,
        forwardingServers: ObjectSet<String>
    ) {
        messagingSenderService.sendData (
            player = player,
            target = target,
            message = message,
            type = type,
            messageID = messageID,
            channel = channel,
            forwardingServers = forwardingServers
        )

        debug("${messagingSenderService.javaClass.name} with hashcode ${messagingSenderService.hashCode()}")
    }

    companion object {
        val IDENTIFIER: MinecraftChannelIdentifier = MinecraftChannelIdentifier.from(SurfChatApi.messagingChannelIdentifier)
    }
}