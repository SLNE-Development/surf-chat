package dev.slne.surf.chat.server.util

import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.minimessage.MiniMessage

fun SurfComponentBuilder.appendMessageData(messageData: MessageData) = append(buildText {
    info("Gesendet von ")
    variableValue(messageData.sender.name)
    info(" am ")
    variableValue(messageData.sentAt.formatTime())
    appendNewline()
    info("Gesendet auf Server ")
    variableValue(messageData.server.name)
})

suspend fun SurfComponentBuilder.appendName(player: CloudPlayer) {
    val prefix = player.getLuckpermsMetaData("prefix")
    append(
        MiniMessage.miniMessage().deserialize("$prefix${player.name}").colorIfAbsent(Colors.WHITE)
    )
}