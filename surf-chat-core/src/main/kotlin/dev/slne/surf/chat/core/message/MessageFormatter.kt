package dev.slne.surf.chat.core.message

import net.kyori.adventure.text.Component

interface MessageFormatter {
    val message: Component
    fun format(): Boolean
}