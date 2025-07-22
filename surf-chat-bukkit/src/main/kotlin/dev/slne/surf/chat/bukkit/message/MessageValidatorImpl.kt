package dev.slne.surf.chat.bukkit.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.plainText
import dev.slne.surf.chat.core.message.MessageValidator
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import java.net.URI
import java.util.*

class MessageValidatorImpl {
    companion object {
        fun stringValidator(message: String): MessageValidator<String> {
            return StringMessageValidator(message, Component.empty())
        }

        fun componentValidator(message: Component): MessageValidator<Component> {
            return ComponentMessageValidator(message, Component.text("???"))
        }
    }

    private class StringMessageValidator(
        override val message: String,
        override var failureMessage: Component
    ) : MessageValidator<String> {
        private val allowedDomains = mutableObjectSetOf<String>()
        private val validCharactersRegex =
            "^[\\u0000-\\u007FäöüÄÖÜß€@£¥|²³µ½¼¾«»¡¿°§´`^~¨]+$".toRegex()
        private val urlRegex =
            "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)
        private val messageTimestamps = mutableObject2ObjectMapOf<UUID, ObjectList<Long>>()

        override fun validate(user: User): Boolean {
            if (user.hasPermission(SurfChatPermissionRegistry.FILTER_BYPASS)) {
                return true
            }

            if (message.isBlank()) {
                failureMessage = buildText {
                    error("Deine Nachricht darf nicht leer sein.")
                }
                return false
            }

            if (this.containsLink(message)) {
                failureMessage = buildText {
                    error("Deine Nachricht enthält einen unerlaubten Link.")
                }
                return false
            }

            if (this.isValidInput(message)) {
                failureMessage = buildText {
                    error("Deine Nachricht enthält unerlaubte Zeichen.")
                }
                return false
            }

            if (this.isSpamming(user.uuid)) {
                failureMessage = buildText {
                    error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.")
                }
                return false
            }

            return true
        }

        private fun containsLink(message: String) = urlRegex.findAll(message).any { match ->
            val rawUrl = match.value
            val formattedUrl =
                if (rawUrl.startsWith("http", ignoreCase = true)) rawUrl else "http://$rawUrl"

            val domain = try {
                URI(formattedUrl).host?.lowercase()?.removePrefix("www.")
            } catch (_: Exception) {
                return@any true
            }

            domain == null || allowedDomains.none { domain.endsWith(it.lowercase()) }
        }

        private fun isValidInput(input: String) = validCharactersRegex.matches(input)
        private fun isSpamming(uuid: UUID): Boolean {
            val currentTime = System.currentTimeMillis()
            val windowStart = currentTime - 3 * 1000 // 3 seconds window
            val timestamps = messageTimestamps.getOrPut(uuid) { mutableObjectListOf<Long>() }

            timestamps.removeIf { it < windowStart }

            if (timestamps.size >= 5) { // the maximum number of messages allowed in the time window
                return true
            }

            timestamps.add(currentTime)
            return false
        }
    }

    private class ComponentMessageValidator(
        override val message: Component,
        override var failureMessage: Component
    ) :
        MessageValidator<Component> {
        override fun validate(user: User): Boolean {
            val validator = stringValidator(message.plainText())
            val success = validator.validate(user)

            failureMessage = validator.failureMessage
            return success
        }
    }
}