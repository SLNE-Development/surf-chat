package dev.slne.surf.chat.paper.processor.pre

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import com.github.benmanes.caffeine.cache.RemovalCause
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.paper.config.configs.CommandTypoConfig
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.util.appendLinePrefix
import dev.slne.surf.chat.paper.util.hasPermission
import dev.slne.surf.chat.paper.util.sendText
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*
import kotlin.time.Duration.Companion.seconds


object CommandTypoPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.COMMAND_TYPO

    private data class PendingTypoConfirmation(
        val data: MessageData,
        val viewers: MutableSet<Audience>
    )

    private val pendingConfirmations = Caffeine.newBuilder()
        .expireAfter(Expiry.creating<UUID, PendingTypoConfirmation> { _, _ ->
            Duration.ofSeconds(commandTypoConfig().confirmationTimeoutSeconds)
        })
        .maximumSize(10_000)
        .withRemovalListener { uuid, confirmation, cause ->
            if (cause != RemovalCause.EXPIRED) return@withRemovalListener

            val player = uuid?.let { server.getPlayer(it) } ?: return@withRemovalListener
            val message = confirmation?.data?.plainMessage ?: return@withRemovalListener
            player.sendText {
                appendErrorPrefix()
                error("Die Nachricht \"")
                variableValue(message)
                error("\" wurde automatisch verworfen.")
            }
        }
        .build<UUID, PendingTypoConfirmation>()
        .asMap()

    private val bypassUntil = Caffeine.newBuilder()
        .expireAfterWrite(10.seconds)
        .maximumSize(10_000)
        .build<UUID, Boolean>()

    private fun commandTypoConfig() = plugin.surfChatConfig.config.commandTypoConfig

    override fun process(context: MessageContext): MessageContext {
        val config = commandTypoConfig()
        if (!config.enabled) {
            return context
        }

        val data = context.messageData

        if (data.sender.hasPermission(PermissionRegistry.BYPASS_COMMAND_TYPO)) {
            return context
        }

        if (bypassUntil.getIfPresent(data.sender) != null) {
            return context
        }

        val plainMessage = data.plainMessage
        val prefix = config.prefixCharacters.firstOrNull { plainMessage.startsWith(it, ignoreCase = true) }
            ?: return context

        val candidate = plainMessage.substring(prefix.length).substringBefore(' ').lowercase()
        if (candidate.isEmpty() || !isKnownCommand(candidate)) {
            return context
        }

        context.cancel()

        pendingConfirmations[data.messageUuid] = PendingTypoConfirmation(data, context.viewers)
        sendConfirmationPrompt(data, config)

        return context
    }

    private fun isKnownCommand(command: String): Boolean {
        return runCatching { server.commandMap.getCommand(command) != null }.getOrDefault(false)
    }

    private fun sendConfirmationPrompt(data: MessageData, config: CommandTypoConfig) {
        data.sender.sendText {
            appendWarningPrefix()
            warning("Wolltest du wirklich eine Chatnachricht senden und keinen Befehl ausführen?")
            appendNewline()
            appendLinePrefix()
            appendSpace()
            variableValue("\"${data.plainMessage}\"")
            appendNewline()
            appendLinePrefix()
            appendSpace()
            append(confirmButton(data, config))
            appendSpace()
            append(discardButton(data, config))
        }
    }

    private fun confirmButton(data: MessageData, config: CommandTypoConfig) = buildText {
        darkSpacer("[")
        success("✔ Nachricht senden")
        darkSpacer("]")
        hoverEvent(buildText {
            success("Klicke, um die Nachricht erneut zu senden.")
        })
        clickEvent(
            ClickEvent.callback({ audience ->
                (audience as? Player)?.let { player ->
                    onConfirm(player, data.messageUuid)
                }
            }) {
                it.uses(1).lifetime(Duration.ofSeconds(config.confirmationTimeoutSeconds))
            }
        )
    }

    private fun discardButton(data: MessageData, config: CommandTypoConfig) = buildText {
        darkSpacer("[")
        error("✘ Nachricht verwerfen")
        darkSpacer("]")
        hoverEvent(buildText {
            error("Klicke, um die Nachricht zu verwerfen.")
        })
        clickEvent(
            ClickEvent.callback({ audience ->
                (audience as? Player)?.let { player ->
                    onDiscard(player, data.messageUuid)
                }
            }) {
                it.uses(1).lifetime(Duration.ofSeconds(config.confirmationTimeoutSeconds))
            }
        )
    }


    private fun onConfirm(player: Player, messageUuid: UUID) {
        val pending = pendingConfirmations.remove(messageUuid)
        if (pending == null) {
            player.sendText {
                appendErrorPrefix()
                error("Diese Bestätigung ist abgelaufen oder wurde bereits verarbeitet.")
            }
            return
        }

        if (player.uniqueId != pending.data.sender) {
            return
        }

        player.sendText {
            appendSuccessPrefix()
            success("Du kannst die Nachricht nun erneut im Chat senden.")
        }

        bypassUntil.put(player.uniqueId, true)
        player.chat(pending.data.plainMessage)
    }

    private fun onDiscard(player: Player, messageUuid: UUID) {
        val pending = pendingConfirmations.remove(messageUuid) ?: return

        if (player.uniqueId != pending.data.sender) {
            return
        }

        player.sendText {
            appendSuccessPrefix()
            success("Die Nachricht ")
            variableValue("\"${pending.data.plainMessage}\"")
            appendSpace()
            success("wurde verworfen.")
        }
    }
}
