package dev.slne.surf.chat.paper.message

import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.chat.core.client.denylist.denylistService
import dev.slne.surf.chat.core.client.functionality.functionalityService
import dev.slne.surf.chat.core.client.result.CharCheckResult
import dev.slne.surf.chat.core.client.result.LinkCheckResult
import dev.slne.surf.chat.core.client.result.SpamCheckResult
import dev.slne.surf.chat.core.common.message.MessageValidator
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.hasPlatformPermission
import dev.slne.surf.chat.paper.util.plainText
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
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
        override fun validate(user: CloudPlayer): MessageValidationResult {
            if (user.hasPlatformPermission(SurfChatPermissionRegistry.TEAM_BYPASS_FILTER)) {
                return MessageValidationResult.Success()
            }

            if (this.checkAutoDisabling(user)) {
                return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.AutoDisabled())
            }

            if (!functionalityService.isLocalChatEnabled() && !user.hasPlatformPermission(
                    SurfChatPermissionRegistry.TEAM_BYPASS_FUNCTIONALITY
                )
            ) {
                return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.ChatDisabled())
            }

            if (message.isBlank()) {
                return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.EmptyContent())
            }

            denylistService.getEntries().find { message.contains(it.word, true) }
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


        fun checkAutoDisabling(player: CloudPlayer): Boolean =
            !player.hasPlatformPermission(SurfChatPermissionRegistry.AUTO_CHAT_DISABLING_BYPASS) && Bukkit.getOnlinePlayers()
                .count() > (getMinAmountForServer() ?: 0) && isDisablingEnabled()

        fun isDisablingEnabled() =
            SyncValues.autoDisablingMinAmounts.any { it.first.matches(CloudServer.current().name) }

        fun getMinAmountForServer(): Int? =
            SyncValues.autoDisablingMinAmounts.firstOrNull { it.first.matches(CloudServer.current().name) }?.second
    }

    private class ComponentMessageValidator(
        override val message: Component
    ) : MessageValidator<Component> {
        override fun validate(user: CloudPlayer): MessageValidationResult {
            val validator = stringValidator(message.plainText())
            val result = validator.validate(user)

            return result
        }
    }
}