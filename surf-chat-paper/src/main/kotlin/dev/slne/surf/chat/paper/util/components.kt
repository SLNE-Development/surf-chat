package dev.slne.surf.chat.paper.util

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.chat.core.client.hook.LuckPermsHook
import dev.slne.surf.chat.core.client.message.format.appendName
import org.bukkit.entity.Player

fun SurfComponentBuilder.appendName(player: Player, allowTeleport: Boolean) =
    appendName(player.name, player.uniqueId, LuckPermsHook.getPrefix(player.uniqueId), allowTeleport)
