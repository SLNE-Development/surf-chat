package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.chat.bukkit.message.MessageDataImpl
import dev.slne.surf.chat.bukkit.message.MessageFormatterImpl
import dev.slne.surf.chat.bukkit.message.MessageValidatorImpl
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.*
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class AsyncChatListener : Listener {
    private val channelExceptPattern =
        Regex("^@(all|a|here|everyone)\\b\\s*", RegexOption.IGNORE_CASE)

    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val time = System.currentTimeMillis()
        val player = event.player
        val user = player.user() ?: return

        val server = plugin.server
        val message = event.message()
        val messageId = UUID.randomUUID()
        val plainMessage = message.plainText()

        val messageFormatter = MessageFormatterImpl(message.remove(channelExceptPattern))
        val validationResult = MessageValidatorImpl.componentValidator(message).validate(user)

        if (validationResult.isFailure()) {
            val error = validationResult.getErrorOrNull() ?: return

            player.sendText {
                appendWarningPrefix()
                append(error.errorMessage)
            }

            if (error is MessageValidationResult.MessageValidationError.DenylistedWord) {
                plugin.launch {
                    denylistActionService.makeAction(
                        error.denylistEntry,
                        event.signedMessage(),
                        user
                    )
                }
            }

            if (
                error is MessageValidationResult.MessageValidationError.BadLink ||
                error is MessageValidationResult.MessageValidationError.BadCharacters ||
                error is MessageValidationResult.MessageValidationError.EmptyContent ||
                error is MessageValidationResult.MessageValidationError.DenylistedWord
            ) {
                sendTeamMessage {
                    appendBotIcon()
                    info("Eine Nachricht von ")
                    variableValue(user.name)
                    info(" wurde blockiert.")
                    appendSpace()
                    info("Grund: ")
                    variableValue(error.name)

                    hoverEvent(buildText {
                        info(plainMessage)
                    })
                }
            }
        }

        val data = MessageDataImpl(
            message,
            user,
            null,
            time,
            messageId,
            server,
            null,
            event.signedMessage(),
            MessageType.GLOBAL
        )

        val channel = channelService.getChannel(user)

        if (channel != null && !channelExceptPattern.containsMatchIn(plainMessage)) {
            event.viewers().clear()
            event.viewers().addAll(channel.members.mapNotNull { it.player() })
            event.viewers()
                .addAll(spyService.getChannelSpies(channel).mapNotNull { Bukkit.getPlayer(it) })
            event.renderer { _, _, _, viewerAudience ->
                val channelData = data.withChannel(channel).withReceiver(viewerAudience.user())

                if (spyService.getChannelSpies(channel).mapNotNull { Bukkit.getPlayer(it) }
                        .contains(viewerAudience)) {
                    return@renderer messageFormatter.formatChannelSpy(
                        channelData
                    )
                }

                messageFormatter.formatChannel(
                    channelData
                )
            }
        } else {
            event.viewers().removeIf { it.isConsole() }
            event.renderer { _, _, _, viewerAudience ->
                messageFormatter.formatGlobal(
                    data.withReceiver(viewerAudience.user())
                )
            }
        }

        plugin.launch {
            historyService.logMessage(
                data.withChannel(channel)
            )
        }
    }
}