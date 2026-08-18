package dev.slne.surf.chat.paper.util

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.chat.core.client.hook.LuckPermsHook
import dev.slne.surf.chat.core.client.message.format.appendName
import org.bukkit.entity.Player

fun SurfComponentBuilder.appendName(player: Player) =
    appendName(player.name, LuckPermsHook.getPrefix(player.uniqueId))
