@file:Suppress("RETURN_IN_FUNCTION_WITH_EXPRESSION_BODY_WARNING")

package dev.slne.surf.chat.core.client.message.format

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.client.hook.LuckPermsHook
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.util.formatTime
import dev.slne.surf.chat.core.client.util.nameOrUnknown
import dev.slne.surf.chat.core.client.util.updateLinks
import dev.slne.surf.chat.core.common.service.DeletionService
import dev.slne.surf.core.api.common.SurfCoreApi
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.UUID

fun SurfComponentBuilder.appendDelete(messageData: MessageData) = append(buildText {
    darkSpacer("[")
    error("X")
    darkSpacer("]")
    darkSpacer(" ")
    clickEvent(ClickEvent.callback { clicked ->
        ChatPlatform.launchAsync {
            val deleted = DeletionService.deleteMessage(
                messageData,
                deleter = clicked,
            )

            if (!deleted) {
                clicked.sendText {
                    appendErrorPrefix()
                    error("Die Nachricht besitzt eine ungültige Signatur und konnte nicht gelöscht werden.")
                }
            }
        }
    })
    hoverEvent(buildText {
        warning("Klicke, um die Nachricht zu löschen")
    })
})

fun SurfComponentBuilder.appendName(name: String, prefix: String) =
    append(MiniMessage.miniMessage().deserialize(prefix + name))

fun buildConnectionMessage(name: String, prefix: String, joined: Boolean): Component = buildText {
    darkSpacer("[")
    if (joined) {
        success("+")
    } else {
        error("-")
    }
    darkSpacer("] ")
    append(miniMessage.deserialize(prefix + name))
}

fun SurfComponentBuilder.appendTeleport(name: String, uuid: UUID) = append {
    darkSpacer("[")
    info("TP")
    darkSpacer("]")
    darkSpacer(" ")
    clickEvent(ClickEvent.callback { clicked ->
        val who = clicked.uuidOrNull() ?: return@callback
        ChatPlatform.teleportToPlayer(who, uuid)
    })
    hoverEvent(buildText {
        info("Klicke, um dich zu $name zu teleportieren")
    })
}

fun SurfComponentBuilder.appendSpyIcon() = append {
    spacer("[")
    info("SPY")
    spacer("]")
}

fun SurfComponentBuilder.appendBotIcon() = append {
    darkSpacer(">>")
    appendSpace()
    error("AUTOMOD", TextDecoration.BOLD)
    appendSpace()
    spacer("|")
    appendSpace()
}

fun SurfComponentBuilder.appendStatusIcon(status: Boolean) = append {
    darkSpacer("[")
    if (status) {
        text("✔", Colors.GREEN)
    } else {
        text("✘", Colors.RED)
    }
    darkSpacer("]")
    appendSpace()
}

fun SurfComponentBuilder.appendLinePrefix() = darkSpacer(">")

fun SurfComponentBuilder.appendMessageData(senderName: String, messageData: MessageData) = append {
    appendLinePrefix()
    appendSpace()
    spacer("von")
    appendSpace()
    variableValue(senderName)

    appendNewline()
    appendLinePrefix()
    appendSpace()
    spacer("am")
    appendSpace()
    variableValue(messageData.sentAt.formatTime())

    appendNewline()
    appendLinePrefix()
    appendSpace()
    spacer("auf")
    appendSpace()
    variableValue(messageData.server)
}

suspend fun formatIncomingPm(messageData: MessageData) = buildText {
    val senderUser = messageData.senderUser()

    darkSpacer(">> ")
    text("PM", Colors.RED)
    darkSpacer(" | ")
    variableValue(senderUser.lastKnownName ?: senderUser.uuid.toString())
    darkSpacer(" -> ")
    variableValue("Dir")
    darkSpacer(" >> ")
    append(updateLinks(messageData.message))
    hoverEvent(buildText {
        appendMessageData(
            senderUser.lastKnownName ?: senderUser.uuid.toString(), messageData
        )
    })
    clickSuggestsCommand("/msg ${senderUser.lastKnownName} ")
}

suspend fun formatOutgoingPm(messageData: MessageData) = buildText {
    val receiverName = messageData.receiverUser()?.lastKnownName ?: "Error"

    darkSpacer(">> ")
    text("PM", Colors.RED)
    darkSpacer(" | ")
    variableValue("Du")
    darkSpacer(" -> ")
    variableValue(receiverName)
    darkSpacer(" >> ")
    append(updateLinks(messageData.message))

    hoverEvent(buildText { appendMessageData(receiverName, messageData) })
    clickSuggestsCommand("/msg $receiverName ")
}

suspend fun formatTeamchat(messageData: MessageData) = buildText {
    val sender = messageData.sender
    val senderName = SurfCoreApi.getOfflinePlayer(sender)?.lastKnownName ?: return Component.empty()

    darkSpacer(">> ")
    text("TEAM", Colors.RED, TextDecoration.BOLD)
    darkSpacer(" | ")
    appendName(senderName, LuckPermsHook.getPrefix(sender))
    darkSpacer(" >> ")
    append(updateLinks(messageData.message))

    hoverEvent(buildText { appendMessageData(senderName, messageData) })
    clickSuggestsCommand("/teamchat ")
}

suspend fun formatPmSpy(messageData: MessageData) = buildText {
    val receiver = messageData.receiver ?: return Component.empty()
    val receiverUser = messageData.receiverUser()
    val receiverName = receiverUser?.lastKnownName ?: return Component.empty()
    val senderName = messageData.senderUser().lastKnownName ?: return Component.empty()

    appendSpyIcon()
    appendSpace()

    if (ChatPlatform.hasPermission(receiver, ChatPermissions.COMMAND_SURFCHAT_TELEPORT)) {
        appendTeleport(receiverName, receiver)
    }

    variableValue(senderName)
    appendSpace()
    darkSpacer("-->")
    appendSpace()
    variableValue(receiverName)
    spacer(":")
    appendSpace()
    append(updateLinks(messageData.message))
    hoverEvent(buildText { appendMessageData(senderName, messageData) })
    clickSuggestsCommand("/msg $senderName ")
}

object Components {
    object Deletion {
        suspend fun createMessageDeletedTeamNotification(deleter: Audience?, data: MessageData) =
            buildText {
                val sender = data.senderUser()
                val senderName = sender.lastKnownName ?: sender.uuid.toString()

                appendInfoPrefix()
                if (deleter != null) {
                    variableValue(deleter.nameOrUnknown())
                    info(" hat eine Nachricht von ")
                } else {
                    info("Es wurde eine Nachricht von ")
                }
                variableValue(senderName)
                info(" gelöscht: ")
                append(data.message)
            }
    }
}
