package dev.slne.surf.chat.bukkit.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.bukkit.message.MessageValidator
import dev.slne.surf.chat.bukkit.processor.ProcessorOrder
import dev.slne.surf.chat.bukkit.util.sendText

object ValidatorPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData
        val senderUuid = data.sender
        val validationResult = MessageValidator.validate(data)

        if (validationResult.isFailure()) {
            val error = validationResult.getErrorOrThrow()

            senderUuid.sendText {
                appendWarningPrefix()
                error(error.errorMessage)
            }

            context.cancel()
        }

        return context
    }
}