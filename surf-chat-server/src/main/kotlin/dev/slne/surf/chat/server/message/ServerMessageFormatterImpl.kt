package dev.slne.surf.chat.server.message

import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.chat.server.util.appendMessageData
import dev.slne.surf.chat.server.util.appendName
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.plain
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration

class ServerMessageFormatterImpl(val message: Component) {
    private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")

    suspend fun formatIncomingPm(messageData: MessageData) = buildText {
        val senderName = messageData.sender.name

        darkSpacer(">> ")
        text("PM", Colors.RED)
        darkSpacer(" | ")
        variableValue(senderName)
        darkSpacer(" -> ")
        variableValue("Dir")
        darkSpacer(" >> ")
        append(updateLinks(messageData.message))
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg $senderName ")
    }

    suspend fun formatOutgoingPm(messageData: MessageData) = buildText {
        val receiverName = messageData.receiver?.name ?: "Error"

        darkSpacer(">> ")
        text("PM", Colors.RED)
        darkSpacer(" | ")
        variableValue("Du")
        darkSpacer(" -> ")
        variableValue(receiverName)
        darkSpacer(" >> ")
        append(updateLinks(messageData.message))

        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg $receiverName ")
    }

    suspend fun formatTeamchat(messageData: MessageData) = buildText {
        val player = messageData.sender

        darkSpacer(">> ")
        text("TEAM", Colors.RED, TextDecoration.BOLD)
        darkSpacer(" | ")
        appendName(player)
        darkSpacer(" >> ")

        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/teamchat ")
    }

    private fun updateLinks(rawMessage: Component): Component {
        var message = rawMessage
        val text = rawMessage.plain()

        linkRegex.findAll(text).filter { text.contains(it.value) }.forEach {
            message = message.replaceText(
                TextReplacementConfig.builder()
                    .match(Regex.escape(it.value))
                    .replacement(
                        buildText {
                            text(it.value)
                            hoverEvent(buildText {
                                info("Klicke hier, um den Link zu öffnen.")
                            })
                            clickOpensUrl(it.value)
                        }
                    )
                    .build()
            )
        }

        return message
    }
}