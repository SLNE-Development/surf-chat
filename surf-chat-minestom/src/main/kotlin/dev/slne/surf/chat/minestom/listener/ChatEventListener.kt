package dev.slne.surf.chat.minestom.listener

import dev.slne.minestom.lobby.api.chat.AsyncChatEvent
import dev.slne.minestom.lobby.api.chat.ChatRenderer
import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.message.redirector.MessageRedirectorRegistry
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.processor.runPostProcessors
import dev.slne.surf.chat.core.client.processor.runPreProcessors
import dev.slne.surf.core.api.common.SurfCoreApi
import java.time.OffsetDateTime
import java.util.*

/**
 * Runs incoming chat messages of this platform through the shared chat pipeline.
 */
object ChatEventListener {
    fun register() {
        AsyncChatEvent.addListener { event ->
            val sentAt = OffsetDateTime.now()
            var data = MessageData(
                event.message,
                UUID.randomUUID(),
                event.player.uuid,
                null,
                sentAt,
                SurfCoreApi.getCurrentServerName(),
                runCatching { event.signedMessage.signature() }.getOrNull(),
                MessageType.GLOBAL
            )

            val result = runPreProcessors(MessageContext(data, event.isCancelled, event.viewers))
            data = result.messageData

            if (result.isCancelled) {
                event.isCancelled = true
            } else {
                event.renderer = ChatRenderer { _, _, _, viewer ->
                    viewer.uuidOrNull()?.let { result.render(it, viewer) } ?: event.message
                }
            }

            val postContext = MessageContext(data, result.isCancelled, event.viewers)
            ChatPlatform.launchAsync {
                runPostProcessors(postContext)
                MessageRedirectorRegistry.redirectors.forEach {
                    it.redirectMessage(event.signedMessage, data)
                }
            }
        }
    }
}
