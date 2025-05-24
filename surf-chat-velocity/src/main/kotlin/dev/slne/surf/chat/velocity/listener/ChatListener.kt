package dev.slne.surf.chat.velocity.listener

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage

class ChatListener : PacketListener {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        val user = event.user

        if (event.packetType != PacketType.Play.Client.CHAT_MESSAGE) {
            return
        }

        val packet = WrapperPlayClientChatMessage(event)

        user.sendMessage("RECEIVED: ${packet.message}")
    }
}
