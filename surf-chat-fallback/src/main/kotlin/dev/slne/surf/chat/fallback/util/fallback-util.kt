package dev.slne.surf.chat.fallback.util

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import org.bukkit.Bukkit

fun sendTeamMessage(message: SurfComponentBuilder.() -> Unit) =
    Bukkit.getOnlinePlayers().filter { it.hasPermission(Constants.TEAM_PERMISSION) }
        .forEach { it.sendText(message) }

fun SurfComponentBuilder.appendBotIcon() = append {
    darkSpacer("[")
    info("ARTY".toSmallCaps())
    darkSpacer("]")
    appendSpace()
}

fun User.toCloudPlayer() = CloudPlayer[this.uuid]
fun User.toOfflineCloudPlayer() = OfflineCloudPlayer[this.uuid]