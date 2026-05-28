package dev.slne.surf.chat.paper.message

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.chat.core.common.service.FunctionalityService
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.hasPermission
import org.bukkit.Bukkit
import java.util.*

object MessageValidator {
    fun validate(messageData: MessageData): MessageValidationResult {
        val sender = messageData.sender
        val message = messageData.plainMessage

        if (sender.hasPermission(PermissionRegistry.BYPASS_FILTER)) {
            return MessageValidationResult.Success()
        }

        if (this.checkAutoDisabling(sender)) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.AutoDisabled())
        }

        if (messageData.type == MessageType.GLOBAL) {
            if (!FunctionalityService.getFunctionalities().localChatEnabled && !sender.hasPermission(
                    PermissionRegistry.BYPASS_FUNCTIONALITY
                )
            ) {
                return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.ChatDisabled())
            }
        }

        if (hasTooManyCapsInARow(message)) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.TooManyCaps())
        }

        if (message.isBlank()) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.EmptyContent())
        }

        return MessageValidationResult.Success()
    }


    fun hasTooManyCapsInARow(message: String): Boolean {
        var consecutiveCaps = 0

        for (char in message) {
            if (char.isUpperCase()) {
                consecutiveCaps++
                if (consecutiveCaps >= plugin.spamConfig.maxUppercaseCharsInRow) {
                    return true
                }
            } else {
                consecutiveCaps = 0
            }
        }

        return false
    }


    fun checkAutoDisabling(player: UUID): Boolean =
        !player.hasPermission(PermissionRegistry.BYPASS_DISABLING)
                && Bukkit.getOnlinePlayers()
            .count() > plugin.autoDisablingConfig.maximumPlayersBeforeDisable
                && plugin.autoDisablingConfig.enabled
}