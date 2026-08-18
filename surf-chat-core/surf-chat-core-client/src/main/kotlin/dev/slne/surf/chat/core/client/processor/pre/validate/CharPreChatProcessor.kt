package dev.slne.surf.chat.core.client.processor.pre.validate

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.processor.ProcessorOrder
import dev.slne.surf.chat.core.client.util.hasPermission
import dev.slne.surf.chat.core.client.util.sendText

private val validCharactersRegex = "^[\\u0020-\\u007EäöüÄÖÜß€@£¥|²³µ½¼¾«»¡¿°§´`^~¨]+$".toRegex()

fun containsIllegalCharacters(message: String) =
    message.any { !validCharactersRegex.matches(it.toString()) }

object CharPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData

        if (context.messageData.sender.hasPermission(ChatPermissions.BYPASS_FILTER)) {
            return context
        }

        if (containsIllegalCharacters(messageData.plainMessage)) {
            messageData.sender.sendText {
                appendWarningPrefix()
                error("Deine Nachricht enthält unerlaubte Zeichen.")
            }

            context.cancel()
            return context
        }

        return context
    }
}
