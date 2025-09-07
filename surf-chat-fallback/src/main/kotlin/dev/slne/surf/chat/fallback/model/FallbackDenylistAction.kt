package dev.slne.surf.chat.fallback.model

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistActionType
import net.kyori.adventure.text.Component

data class FallbackDenylistAction(
    override val name: String,
    override val actionType: DenylistActionType,
    override val reason: Component
) : DenylistAction