package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.message.MessageFormatterImpl
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.spy.spyService
import dev.slne.surf.chat.paper.util.player
import org.bukkit.Bukkit
import org.springframework.stereotype.Component

@Component
class ChannelPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.CHANNEL

    private val channelExceptPattern =
        Regex("^@(all|a|here|everyone)\\b\\s*", RegexOption.IGNORE_CASE)

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData
        val sender = data.sender
        val channel = channelService.getChannel(sender) ?: return context

        if (channelExceptPattern.containsMatchIn(data.plainMessage)) {
            return context
        }

        val messageFormatter = MessageFormatterImpl(data.message)

        context.viewers.clear()
        context.viewers.addAll(channel.members.mapNotNull { it.player() })
        context.viewers
            .addAll(spyService.getChannelSpies(channel).mapNotNull { Bukkit.getPlayer(it) })
        context.messageData = data.withChannel(channel)

        context.render = { viewer ->
            var finalMessage: net.kyori.adventure.text.Component

            val channelData = data.withChannel(channel).withReceiver(viewer)

            if (spyService.getChannelSpies(channel).contains(viewer.uuid)) {
                finalMessage = messageFormatter.formatChannelSpy(
                    channelData
                )
            }

            finalMessage = messageFormatter.formatChannel(
                channelData
            )

            finalMessage
        }

        return context
    }
}