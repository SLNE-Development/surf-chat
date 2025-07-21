package dev.slne.surf.chat.bukkit.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.message.MessageData
import net.kyori.adventure.text.Component

data class MessageDataImpl(
    override val message: Component,
    override val sender: User,
    override val receiver: User?,
    override val sentAt: Long
) : MessageData
