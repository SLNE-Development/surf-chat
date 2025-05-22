package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import com.google.common.io.ByteStreams
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.utils.gson
import dev.slne.surf.chat.core.service.messaging.MessagingSenderService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.entity.Player
import java.util.*

@AutoService(MessagingSenderService::class)
class BukkitMessagingSenderService : MessagingSenderService, Fallback {
    private var currentServer: String = "Unknown"
    private var forwardingServers = ObjectArraySet<String>()

    override fun loadServers() {
        this.currentServer =
            plugin.config.getString("cross-server-messages.current-server") ?: "Unknown"
        this.forwardingServers =
            ObjectArraySet(plugin.config.getStringList("cross-server-messages.forward-to"))
    }

    override fun sendData(
        player: String,
        target: String,
        message: Component,
        type: MessageType,
        messageID: UUID,
        channel: String,
        forwardingServers: ObjectSet<String>
    ) {
        val out = ByteStreams.newDataOutput()

        out.writeUTF(player)
        out.writeUTF(target)
        out.writeUTF(GsonComponentSerializer.gson().serialize(message))
        out.writeUTF(gson.toJson(type))
        out.writeUTF(messageID.toString())
        out.writeUTF(channel)
        out.writeUTF(gson.toJson(forwardingServers))

        plugin.server.sendPluginMessage(
            plugin,
            SurfChatApi.MESSAGING_CHANNEL_IDENTIFIER,
            out.toByteArray()
        )
    }

    override fun sendTeamChatMessage(player: Audience, message: Component) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF(GsonComponentSerializer.gson().serialize(message))

        if (player !is Player) {
            return
        }

        player.sendPluginMessage(plugin, SurfChatApi.TEAM_CHAT_IDENTIFIER, out.toByteArray())
    }

    companion object {
        fun getCurrentServer(): String {
            return (MessagingSenderService.INSTANCE as BukkitMessagingSenderService).currentServer
        }

        fun getForwardingServers(): ObjectSet<String> {
            return (MessagingSenderService.INSTANCE as BukkitMessagingSenderService).forwardingServers
        }
    }
}