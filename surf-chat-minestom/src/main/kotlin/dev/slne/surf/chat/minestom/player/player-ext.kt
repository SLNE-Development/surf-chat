package dev.slne.surf.chat.minestom.player

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player

/**
 * The name a signed message of this player is bound to: their display name, falling back to their
 * username, with the username as insertion and the player as hover.
 */
fun Player.boundDisplayName(): Component = (displayName ?: Component.text(username))
    .insertion(username)
    .hoverEvent(this)
