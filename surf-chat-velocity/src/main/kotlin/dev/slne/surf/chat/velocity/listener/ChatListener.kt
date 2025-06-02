package dev.slne.surf.chat.velocity.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.util.crypto.SaltSignature
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage

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

    fun SaltSignature.readable(): String {
        return "SaltSignature(salt=${this.salt}, signature=${this.signature})"
    }
}
