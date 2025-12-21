package dev.slne.surf.chat.bukkit.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.functionalityService
import org.bukkit.Bukkit

class MessageValidator {
    fun validate(messageData: MessageData): MessageValidationResult {
        val user = messageData.sender
        val message = messageData.plainMessage

        if (user.hasPermission(SurfChatPermissionRegistry.TEAM_BYPASS_FILTER)) {
            return MessageValidationResult.Success()
        }

        if (this.checkAutoDisabling(user)) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.AutoDisabled())
        }

        if (!functionalityService.isLocalChatEnabled() && !user.hasPermission(
                SurfChatPermissionRegistry.TEAM_BYPASS_FUNCTIONALITY
            )
        ) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.ChatDisabled())
        }

        if (message.isBlank()) {
            return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.EmptyContent())
        }

        denylistService.getLocalEntries().find { message.contains(it.word, true) }
            ?.let { entry ->
                return MessageValidationResult.Failure(
                    MessageValidationResult.MessageValidationError.DenylistedWord(
                        entry
                    )
                )
            }

        return MessageValidationResult.Success()
    }


    fun checkAutoDisabling(player: User): Boolean =
        !player.hasPermission(SurfChatPermissionRegistry.AUTO_CHAT_DISABLING_BYPASS)
                && Bukkit.getOnlinePlayers()
            .count() > plugin.autoDisablingConfig.maximumPlayersBeforeDisable
                && plugin.autoDisablingConfig.enabled
}