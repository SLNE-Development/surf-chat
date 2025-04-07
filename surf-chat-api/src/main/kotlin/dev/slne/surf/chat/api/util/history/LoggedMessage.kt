package dev.slne.surf.chat.api.util.history

import net.kyori.adventure.text.Component

data class LoggedMessage(val sender: String, val receiver: String, val message: Component)
