package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.gson
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.messaging.MessagingSenderService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback

@AutoService(MessagingSenderService::class)
class BukkitMessagingSenderService (
    override var forwardingServers: ObjectSet<String>,
    override var currentServer: String
): MessagingSenderService, Fallback {
    override fun loadServers() {
        this.currentServer = plugin.config.getString("cross-server-messages.current-server") ?: "Unknown"
        this.forwardingServers = ObjectArraySet(plugin.config.getStringList("cross-server-messages.forward-to"))
    }

    override fun sendData (
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: Long,
        channel: String
    ) {
        val out = ByteStreams.newDataOutput()

        out.writeUTF(currentServer)
        out.writeUTF(gson.toJson(forwardingServers))
        out.writeUTF(player)
        out.writeUTF(target)
        out.writeUTF(gson.toJson(message))
        out.writeUTF(gson.toJson(type))
        out.writeLong(messageID)
        out.writeUTF(channel)

        plugin.server.sendPluginMessage(plugin, SurfChatApi.messagingChannelIdentifier, out.toByteArray())
    }
}