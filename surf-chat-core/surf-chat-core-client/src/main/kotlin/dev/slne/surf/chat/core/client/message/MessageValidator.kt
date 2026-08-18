package dev.slne.surf.chat.core.client.message

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.chat.core.client.config.chatConfig
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.util.hasPermission
import dev.slne.surf.chat.core.common.service.FunctionalityService
import java.util.UUID

fun hasTooManyCapsInARow(message: String, maxInRow: Int): Boolean {
    var consecutiveCaps = 0

    for (char in message) {
        if (char.isUpperCase()) {
            consecutiveCaps++
            if (consecutiveCaps >= maxInRow) {
                return true
            }
        } else {
            consecutiveCaps = 0
        }
    }

    return false
}

object MessageValidator {
    fun validate(messageData: MessageData): MessageValidationResult {
        val sender = messageData.sender
        val message = messageData.plainMessage

        if (sender.hasPermission(ChatPermissions.BYPASS_FILTER)) {
            return MessageValidationResult.Success()
        }

        if (this.checkAutoDisabling(sender)) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.AutoDisabled())
        }

        if (messageData.type == MessageType.GLOBAL) {
            if (!FunctionalityService.getFunctionalities().localChatEnabled && !sender.hasPermission(
                    ChatPermissions.BYPASS_FUNCTIONALITY
                )
            ) {
                return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.ChatDisabled())
            }
        }

        if (hasTooManyCapsInARow(message, chatConfig.spamConfig.maxUppercaseCharsInRow)) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.TooManyCaps())
        }

        if (message.isBlank()) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.EmptyContent())
        }

        return MessageValidationResult.Success()
    }

    fun checkAutoDisabling(player: UUID): Boolean =
        !player.hasPermission(ChatPermissions.BYPASS_DISABLING)
                && ChatPlatform.onlinePlayerCount() > chatConfig.autoDisablingConfig.maximumPlayersBeforeDisable
                && chatConfig.autoDisablingConfig.enabled
}
