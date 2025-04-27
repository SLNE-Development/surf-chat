package dev.slne.surf.chat.velocity.service

import com.google.auto.service.AutoService
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
import dev.slne.surf.chat.velocity.messageChannel
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.chat.velocity.util.debug
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.util.Services

import java.util.*

@AutoService(MessagingReceiverService::class)
class VelocityMessagingReceiverService(): MessagingReceiverService, Services.Fallback {
    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        if(!event.identifier.equals(messageChannel)) {
            return
        }

        event.result = PluginMessageEvent.ForwardResult.handled()

        if (event.source !is ServerConnection) {
            return
        }

        val input = ByteStreams.newDataInput(event.data)

        val sender = input.readUTF()
        val target = input.readUTF()
        val message = GsonComponentSerializer.gson().deserialize(input.readUTF())
        val type = gson.fromJson(input.readUTF(), ChatMessageType::class.java)
        val messageId = UUID.fromString(input.readUTF())
        val chatChannel = input.readUTF()
        val forwardingServers: Set<String> = gson.fromJson(input.readUTF(), object : TypeToken<Set<String>>() {}.type)

        handleReceive (
            player = sender,
            target = target,
            message = message,
            type = type,
            messageID = messageId,
            channel = chatChannel,
            forwardingServers.toObjectSet()
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
}