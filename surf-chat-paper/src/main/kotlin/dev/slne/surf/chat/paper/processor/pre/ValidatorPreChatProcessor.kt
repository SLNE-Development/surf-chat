package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundTeamMessagePacket
import dev.slne.surf.chat.paper.message.MessageValidatorImpl
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.util.appendBotIcon
import dev.slne.surf.chat.paper.util.appendWarningPrefix
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Component

@Component
class ValidatorPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE

    private val validator by lazy {
        ChatContextHolderImpl.instance.context.getBean<MessageValidatorImpl>()
    }

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData
        val player = data.sender
        val validationResult = validator.validate(data)

        if (validationResult.isFailure()) {
            val error = validationResult.getErrorOrThrow()

            player.sendText {
                appendWarningPrefix()
                error(error.first)
            }

            if (error.second) {
                ServerboundTeamMessagePacket(
                    GsonComponentSerializer.gson().serialize(buildText {
                        appendBotIcon()
                        info("Eine Nachricht von ")
                        variableValue(player.name)
                        info(" wurde blockiert.")

                        hoverEvent(buildText {
                            info(data.plainMessage)
                        })
                    })

                ).fireAndForget()
            }

            context.cancel()
        }

        return context
    }
}