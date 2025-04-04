package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import java.util.UUID

class CompletedComponents {
    fun getDeleteComponent(messageID: UUID): Component = buildText {
        darkSpacer("[")
        error("DEL")
        darkSpacer("]")
        darkSpacer(" ")
        clickEvent(ClickEvent.runCommand("/surfchat delete $messageID"))
        hoverEvent(HoverEvent.showText(buildText {
            error("Klicke, um die Nachricht zu löschen")
        }))
    }

    fun getTeleportComponent(name: String): Component = buildText {
        darkSpacer("[")
        info("TP")
        darkSpacer("]")
        darkSpacer(" ")
        clickEvent(ClickEvent.runCommand("/tp $name"))
        hoverEvent(HoverEvent.showText(buildText {
            info("Klicke, um dich zu $name zu teleportieren")
        }))
    }

    fun getChannelComponent(channel: String): Component = buildText {
        darkSpacer("[")
        info(channel)
        darkSpacer("]")
        darkSpacer(" ")
    }


    companion object {
        val INSTANCE = CompletedComponents()
    }
}

val components get() = CompletedComponents.INSTANCE