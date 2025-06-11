package dev.slne.surf.chat.velocity.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_19_3
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.util.crypto.SaltSignature
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChatListener : PacketListener {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        val user = event.user

        if (event.packetType != PacketType.Play.Client.CHAT_MESSAGE) {
            return
        }

        val packet = WrapperPlayClientChatMessage(event)
        val signingData = packet.messageSignData

        if(signingData.isEmpty) {
            user.sendMessage("RECEIVED SIGNED: ${packet.message}")
            return
        }

        //packet.message = "[Modified] ${packet.message}"

        packet.writeSaltSignature(signingData.get().saltSignature)

        user.sendMessage("RECEIVED SIGNED: ${packet.message} with salt signature: ${signingData.get().saltSignature.readable()}")
    }

    override fun onPacketSend(event: PacketSendEvent) {
        val user = event.user

        if (event.packetType != PacketType.Play.Server.CHAT_MESSAGE) {
            return
        }

        val packet = WrapperPlayServerChatMessage(event)
        val message = packet.message as? ChatMessage_v1_19_3 ?: return run {
            user.sendMessage("Message is not of type ChatMessage_v1_19_3, skipping formatting.")
        }

        user.sendMessage("SENT: ${message.plainContent}")

        message.chatFormatting.name = buildText {
            error("TROLL")
        }

        //message.plainContent = "<red>Formatted: <yellow>${message.plainContent}"

        packet.message = message

        user.sendMessage("SENT FORMATTED: ${message.plainContent}")
    }

    fun SaltSignature.readable(): String {
        return "SaltSignature(salt=${this.salt}, signature=${this.signature})"
    }
}
