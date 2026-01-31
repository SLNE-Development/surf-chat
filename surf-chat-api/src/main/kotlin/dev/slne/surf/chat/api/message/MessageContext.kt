package dev.slne.surf.chat.api.message

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import java.util.*

data class MessageContext(
    var messageData: MessageData,
    var isCancelled: Boolean,
    val viewers: MutableSet<Audience>,
    var render: MessageRenderer = defaultRenderer()
) {
    typealias MessageRenderer = (viewerUUID: UUID, viewAudience: Audience) -> Component

    fun cancel() {
        isCancelled = true
    }

    fun edit(editBlock: MessageContext.() -> Unit): MessageContext {
        editBlock()
        return this
    }

    companion object {
        fun defaultRenderer(): MessageRenderer = { _, _ ->
            buildText {
                appendErrorPrefix()
                error("Internal chat formatting error: no renderer set for message context")
            }
        }
    }
}