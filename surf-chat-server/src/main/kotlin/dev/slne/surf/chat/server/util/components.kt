package dev.slne.surf.chat.server.util

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.minimessage.MiniMessage

suspend fun SurfComponentBuilder.appendName(player: CloudPlayer) {
    val prefix = player.getLuckpermsMetaData("prefix")
    val pString = prefix ?: ""
    append(
        MiniMessage.miniMessage().deserialize("$pString${player.name}").colorIfAbsent(Colors.WHITE)
    )
}