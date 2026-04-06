package dev.slne.surf.chat.api.message

import dev.slne.surf.api.core.messages.adventure.buildText
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import java.util.*

typealias MessageContextRenderer = (viewerUUID: UUID, viewAudience: Audience) -> Component

data class MessageContext(
    var messageData: MessageData,
    var isCancelled: Boolean,
    val viewers: MutableSet<Audience>,
    var render: MessageContextRenderer = defaultRenderer()
) {

    fun cancel() {
        isCancelled = true
    }

    fun edit(editBlock: MessageContext.() -> Unit): MessageContext {
        editBlock()
        return this
    }

    companion object {
        fun defaultRenderer(): MessageContextRenderer = { _, _ ->
            buildText {
                appendErrorPrefix()
                error("Internal chat formatting error: no renderer set for message context")
            }
        }
    }
}