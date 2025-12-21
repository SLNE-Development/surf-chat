package dev.slne.surf.chat.api.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

data class MessageContext(
    var messageData: MessageData,
    var isCancelled: Boolean,
    val viewers: MutableSet<Audience>,
    var render: (viewer: User) -> Component = defaultRenderer()
) {
    fun cancel() {
        isCancelled = true
    }

    fun edit(editBlock: MessageContext.() -> Unit): MessageContext {
        editBlock()
        return this
    }

    companion object {
        fun defaultRenderer(): (viewer: User) -> Component {
            return { _ ->
                buildText {
                    appendPrefix()
                    error("Internal chat formatting error: no renderer set for message context")
                }
            }
        }
    }
}