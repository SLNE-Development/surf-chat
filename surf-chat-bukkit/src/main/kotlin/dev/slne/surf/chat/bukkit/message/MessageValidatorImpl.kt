package dev.slne.surf.chat.bukkit.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.chat.bukkit.message.result.CharCheckResult
import dev.slne.surf.chat.bukkit.message.result.LinkCheckResult
import dev.slne.surf.chat.bukkit.message.result.SpamCheckResult
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.plainText
import dev.slne.surf.chat.core.message.MessageValidator
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.functionalityService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class MessageValidatorImpl {
    companion object {
        fun stringValidator(message: String): MessageValidator<String> {
            return StringMessageValidator(message)
        }

        fun componentValidator(message: Component): MessageValidator<Component> {
            return ComponentMessageValidator(message)
        }
    }

    private class StringMessageValidator(
        override val message: String
    ) : MessageValidator<String> {
        override fun validate(user: User): MessageValidationResult {
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

            CharCheckResult.of(message).invalidChars?.let {
                return MessageValidationResult.Failure(
                    MessageValidationResult.MessageValidationError.BadCharacters(
                        it
                    )
                )
            }

            LinkCheckResult.of(message).link?.let {
                return MessageValidationResult.Failure(
                    MessageValidationResult.MessageValidationError.BadLink(
                        it
                    )
                )
            }

            SpamCheckResult.of(user.uuid).waitSeconds?.let {
                return MessageValidationResult.Failure(
                    MessageValidationResult.MessageValidationError.TooOften(
                        it
                    )
                )
            }

            return MessageValidationResult.Success()
        }


        fun checkAutoDisabling(player: User): Boolean =
            !player.hasPermission(SurfChatPermissionRegistry.AUTO_CHAT_DISABLING_BYPASS)
                    && Bukkit.getOnlinePlayers().size > plugin.autoDisablingConfig.maximumPlayersBeforeDisable
                    && plugin.autoDisablingConfig.enabled
    }

    private class ComponentMessageValidator(
        override val message: Component
    ) : MessageValidator<Component> {
        override fun validate(user: User): MessageValidationResult {
            val validator = stringValidator(message.plainText())
            val result = validator.validate(user)

            return result
        }
    }
}