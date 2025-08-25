package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch

import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.message.MessageDataImpl
import dev.slne.surf.chat.bukkit.message.MessageFormatterImpl
import dev.slne.surf.chat.bukkit.message.MessageValidatorImpl
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.*
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.functionalityService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextReplacementConfig

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

import java.util.*
import kotlin.jvm.optionals.getOrNull

class AsyncChatListener : Listener {
    private val channelExceptPattern =
        Regex("^@(all|a|here|everyone)\\b\\s*", RegexOption.IGNORE_CASE)

    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val player = event.player
        val user = player.user() ?: return

        val message = event.message()
        val messageId = UUID.randomUUID()
        val messageValidator = MessageValidatorImpl.componentValidator(message)
        val server = plugin.server.getOrNull() ?: ChatServer.default()
        val plainMessage = message.plainText()

        if (!functionalityService.isLocalChatEnabled() && !player.hasPermission(
                SurfChatPermissionRegistry.TEAM_ACCESS
            )
        ) {
            player.sendText {
                appendWarningPrefix()
                error("Der Chat ist vorübergehend deaktiviert.")
            }
            event.cancel()
            return
        }

        val cleanedMessage = if (channelExceptPattern.containsMatchIn(plainMessage)) {
            message.replaceText(
                TextReplacementConfig.builder()
                    .match(channelExceptPattern.pattern)
                    .replacement("")
                    .build()
            )
        } else {
            message
        }
        val messageFormatter = MessageFormatterImpl(cleanedMessage)

        if (!messageValidator.isSuccess(user)) {
            event.cancel()

            player.sendText {
                appendPrefix()
                append(messageValidator.failureMessage)
            }
            return
        }

        val channel = channelService.getChannel(user)
        val time = System.currentTimeMillis()

        if (channel != null && !channelExceptPattern.containsMatchIn(plainMessage)) {
            event.viewers().clear()
            event.viewers().addAll(channel.members.mapNotNull { it.player() })
            event.viewers()
                .addAll(spyService.getChannelSpies(channel).mapNotNull { Bukkit.getPlayer(it) })
            event.renderer { _, _, _, viewerAudience ->
                val data = MessageDataImpl(
                    message,
                    user,
                    viewerAudience.user(),
                    time,
                    messageId,
                    server,
                    channel,
                    event.signedMessage(),
                    MessageType.CHANNEL
                )

                if (spyService.getChannelSpies(channel).mapNotNull { Bukkit.getPlayer(it) }
                        .contains(viewerAudience)) {
                    return@renderer messageFormatter.formatChannelSpy(
                        data
                    )
                }

                messageFormatter.formatChannel(
                    data
                )
            }
        } else {
            event.viewers().removeIf { it.isConsole() } //TODO: Add config option
            event.renderer { _, _, _, viewerAudience ->
                messageFormatter.formatGlobal(
                    MessageDataImpl(
                        message,
                        user,
                        viewerAudience.user(),
                        time,
                        messageId,
                        server,
                        null,
                        event.signedMessage(),
                        MessageType.GLOBAL
                    )
                )
            }
        }

        plugin.launch {
            historyService.logMessage(
                MessageDataImpl(
                    message,
                    user,
                    null,
                    time,
                    messageId,
                    server,
                    channel,
                    event.signedMessage(),
                    if (channel != null) MessageType.CHANNEL else MessageType.GLOBAL
                )
            )
        }
    }
}