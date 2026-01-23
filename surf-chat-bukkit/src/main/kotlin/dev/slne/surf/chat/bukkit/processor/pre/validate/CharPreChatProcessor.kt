package dev.slne.surf.chat.bukkit.processor.pre.validate

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.processor.ProcessorOrder
import dev.slne.surf.chat.bukkit.util.hasPermission
import dev.slne.surf.chat.bukkit.util.sendText

object CharPreChatProcessor : PreChatProcessor {
    private val validCharactersRegex =
        "^[\\u0020-\\u007EäöüÄÖÜß€@£¥|²³µ½¼¾«»¡¿°§´`^~¨]+$".toRegex()
    override val order = ProcessorOrder.VALIDATE

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData

        if (context.messageData.sender.hasPermission(PermissionRegistry.BYPASS_FILTER)) {
            return context
        }

        if (messageData.plainMessage.any { !validCharactersRegex.matches(it.toString()) }) {
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