package dev.slne.surf.chat.paper.hook

import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

object MiniPlaceholdersHook {
    fun parse(player: Player, input: String): Component =
        MiniMessage.miniMessage().deserialize(input, player, MiniPlaceholders.audienceGlobalPlaceholders())
}