package dev.slne.surf.chat.bukkit.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.processor.ProcessorOrder
import dev.slne.surf.chat.bukkit.util.player
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.spyService
import org.bukkit.Bukkit

object ChannelPreChatProcessor : PreChatProcessor {
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

        val messageFormatter = MessageFormatter()

        context.viewers.clear()
        context.viewers.addAll(channel.members.mapNotNull { it.player() })
        context.viewers
            .addAll(spyService.getChannelSpies(channel).mapNotNull { Bukkit.getPlayer(it) })
        context.messageData = data.withChannel(channel)

        context.render = { viewer ->
            var finalMessage: net.kyori.adventure.text.Component

            val channelData = data.withChannel(channel).withReceiver(viewer)

            finalMessage = if (spyService.getChannelSpies(channel).contains(viewer.uuid)) {
                messageFormatter.formatChannelSpy(
                    channelData
                )
            } else {
                messageFormatter.formatChannel(
                    channelData
                )
            }

            finalMessage
        }

        return context
    }
}