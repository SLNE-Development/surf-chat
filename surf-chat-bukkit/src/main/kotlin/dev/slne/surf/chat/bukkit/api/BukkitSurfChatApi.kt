package dev.slne.surf.chat.bukkit.api

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.entity.Player
import java.util.*

@AutoService(SurfChatApi::class)
class BukkitSurfChatApi(): SurfChatApi, Fallback {
    override fun logMessage(player: UUID, message: Component) {
        TODO("Not yet implemented")
    }

    override fun sendText(player: Player, message: Component) {
        val finalMessage = buildText {
            appendPrefix()
            append(message)
        }

        surfChatApi.logMessage(player.uniqueId, finalMessage)

        player.sendText {
            append(finalMessage)
        }
    }

    override fun sendRawText(player: Player, message: Component) {
        surfChatApi.logMessage(player.uniqueId, message)

        player.sendText {
            append(message)
        }
    }
}