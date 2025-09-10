package dev.slne.surf.chat.bukkit.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.plainText
import dev.slne.surf.chat.core.message.MessageValidator
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.net.URI
import java.util.*

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
        private val allowedDomains = mutableObjectSetOf<String>()
        private val validCharactersRegex =
            "^[\\u0000-\\u007FäöüÄÖÜß€@£¥|²³µ½¼¾«»¡¿°§´`^~¨]+$".toRegex()
        private val urlRegex =
            "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)
        private val messageTimestamps = mutableObject2ObjectMapOf<UUID, ObjectList<Long>>()

        override fun validate(user: User): MessageValidationResult {
            if (user.hasPermission(SurfChatPermissionRegistry.FILTER_BYPASS)) {
                return MessageValidationResult.Success()
            }

            if (this.checkAutoDisabling(user)) {
                return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.AutoDisabled())
            }

            if (!functionalityService.isLocalChatEnabled() && !user.hasPermission(
                    SurfChatPermissionRegistry.TEAM_ACCESS
                )
            ) {
                return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.ChatDisabled())
            }

            if (message.isBlank()) {
                return MessageValidationResult.Failure(MessageValidationResult.MessageValidationError.EmptyContent())
            }

            denylistService.getLocalEntries().find { message.contains(it.word, true) }
                ?.let { entry ->
                    Bukkit.getOnlinePlayers()
                        .filter { it.hasPermission(SurfChatPermissionRegistry.TEAM_ACCESS) }
                        .forEach {
                            it.sendText {
                                appendPrefix()
                                info("Eine Nachricht von ")
                                variableValue(user.name)
                                info(" wurde blockiert: ")
                                variableValue(message)
                            }
                        }

                    return MessageValidationResult.Failure(
                        MessageValidationResult.MessageValidationError.DenylistedWord(
                            entry
                        )
                    )
                }

            this.containsLink(message).second?.let {
                return MessageValidationResult.Failure(
                    MessageValidationResult.MessageValidationError.BadLink(
                        it
                    )
                )
            }

            val pair = this.isValidInput(message)

            if (pair.first) {
                return MessageValidationResult.Failure(
                    MessageValidationResult.MessageValidationError.BadCharacters(
                        pair.second
                    )
                )
            }

            this.isSpamming(user.uuid).second?.let {
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
                    && Bukkit.getOnlinePlayers().size > plugin.autoDisablingConfig.config.maximumPlayersBeforeDisable
                    && plugin.autoDisablingConfig.config.enabled

        private fun containsLink(message: String): Pair<Boolean, String?> {
            urlRegex.findAll(message).forEach { match ->
                val rawUrl = match.value
                val formattedUrl =
                    if (rawUrl.startsWith("http", ignoreCase = true)) rawUrl else "http://$rawUrl"

                val domain = try {
                    URI(formattedUrl).host?.lowercase()?.removePrefix("www.")
                } catch (_: Exception) {
                    return Pair(true, rawUrl)
                }

                if (domain == null || allowedDomains.none { domain.endsWith(it.lowercase()) }) {
                    return Pair(true, rawUrl)
                }
            }
            return Pair(false, null)
        }

        private fun isValidInput(input: String) = validCharactersRegex.matches(input) to input
        private fun isSpamming(uuid: UUID): Pair<Boolean, Long?> {
            val currentTime = System.currentTimeMillis()
            val windowStart = currentTime - 3_000
            val timestamps = messageTimestamps.getOrPut(uuid) { mutableObjectListOf<Long>() }

            timestamps.removeIf { it < windowStart }

            return if (timestamps.size >= 2) {
                val earliest = timestamps.minOrNull() ?: currentTime
                val waitTimeMs = (earliest + 3_000 - currentTime).coerceAtLeast(0)
                val waitSeconds = (waitTimeMs / 1000) + 1
                false to waitSeconds
            } else {
                timestamps.add(currentTime)
                true to null
            }
        }

    }

    private class ComponentMessageValidator(
        override val message: Component
    ) :
        MessageValidator<Component> {
        override fun validate(user: User): MessageValidationResult {
            val validator = stringValidator(message.plainText())
            val result = validator.validate(user)

            return result
        }
    }
}