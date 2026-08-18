package dev.slne.surf.chat.core.client.processor.pre

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.client.message.MessageValidator
import dev.slne.surf.chat.core.client.processor.ProcessorOrder
import dev.slne.surf.chat.core.client.util.sendText

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
                append(error.errorMessage.colorIfAbsent(Colors.ERROR))
            }

            context.cancel()
        }

        return context
    }
}
