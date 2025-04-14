package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.type.MessageValidationResult
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface MessageValidatorModel {
    fun validate(message: Component, type: ChatMessageType, sender: Player): MessageValidationResult
    fun parse(message: Component, type: ChatMessageType, player: Player, onSuccess: () -> Unit)
}