package dev.slne.surf.chat.fallback.util

import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import org.bukkit.Bukkit

fun sendTeamMessage(message: SurfComponentBuilder.() -> Unit) =
    Bukkit.getOnlinePlayers().filter { it.hasPermission(Constants.PERMISSION_TEAMCHAT) }
        .forEach { it.sendText(message) }

fun SurfComponentBuilder.appendBotIcon() = append {
    darkSpacer("[")
    info("ARTY".toSmallCaps())
    darkSpacer("]")
    appendSpace()
}