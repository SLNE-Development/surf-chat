package dev.slne.surf.chat.core.entry

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistActionType
import net.kyori.adventure.text.Component

data class DenylistActionImpl(
    override val name: String,
    override val actionType: DenylistActionType,
    override val reason: Component,
    override val duration: Long?
) : DenylistAction