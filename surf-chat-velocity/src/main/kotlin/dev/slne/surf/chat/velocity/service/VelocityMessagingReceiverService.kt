package dev.slne.surf.chat.velocity.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams
import com.google.gson.reflect.TypeToken

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.core.service.messaging.MessagingReceiverService
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import dev.slne.surf.chat.velocity.gson
import dev.slne.surf.chat.velocity.messageChannel
import dev.slne.surf.chat.velocity.teamChatChannel
import dev.slne.surf.chat.velocity.teamMembers
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.util.Services

import java.util.*

@AutoService(MessagingReceiverService::class)
class VelocityMessagingReceiverService(): MessagingReceiverService, Services.Fallback {
    @Subscribe
    fun onPluginMessage(event: PluginMessageEvent) {
        when(event.identifier) {
            messageChannel -> {
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

                handleReceive(
                    player = sender,
                    target = target,
                    message = message,
                    type = type,
                    messageID = messageId,
                    channel = chatChannel,
                    forwardingServers.toObjectSet()
                )
            }

            teamChatChannel -> {
                event.result = PluginMessageEvent.ForwardResult.handled()

                if (event.source !is ServerConnection) {
                    return
                }

                val input = ByteStreams.newDataInput(event.data)
                val message = GsonComponentSerializer.gson().deserialize(input.readUTF())

                handeTeamChatReceive(message)
            }
        }
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
    }

    var lastMessage: Component = Component.empty()

    override fun handeTeamChatReceive(message: Component) {
        if(lastMessage == message) {
            return
        }

        lastMessage = message

        for (teamMember in teamMembers()) {
            teamMember.sendMessage(message)
        }
    }
}