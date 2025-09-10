package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.entry.DenylistActionType
import net.kyori.adventure.text.Component

interface DenylistAction {
    val name: String
    val actionType: DenylistActionType
    val reason: Component
    val duration: Long
}