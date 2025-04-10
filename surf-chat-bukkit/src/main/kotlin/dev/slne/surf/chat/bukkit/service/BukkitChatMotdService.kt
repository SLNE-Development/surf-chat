package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.ChatMotdService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.gradle.internal.impldep.it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

@AutoService(ChatMotdService::class)
class BukkitChatMotdService(): ChatMotdService, Fallback {
    private var chatMotdLines: Int2ObjectOpenHashMap<Component> = Int2ObjectOpenHashMap<Component>()
    private var status = true

    override fun loadMotd() {
        val lines = plugin.config.getStringList("chat-message-of-the-day.lines")

        chatMotdLines.clear()

        var line = 0

        lines.map {
            MiniMessage.miniMessage().deserialize(it).appendNewline()
        }.forEach {
            line++
            chatMotdLines[line] = it
        }

        status = plugin.config.getBoolean("chat-message-of-the-day-status", true)
    }

    override fun saveMotd() {
        plugin.config.set("chat-message-of-the-day.lines", chatMotdLines.map { PlainTextComponentSerializer.plainText().serialize(it) })
        plugin.config.set("chat-message-of-the-day-status", status)
        plugin.saveConfig()
    }

    override fun enableMotd() {
        status = true
    }

    override fun disableMotd() {
        status = false
    }

    override fun getMotd(): Component {
        var component = Component.empty()

        this.chatMotdLines.values.forEach {
            component = component.append(it)
        }

        return component
    }

    override fun setMotdLine(line: Int, message: String) {
        chatMotdLines.put(line, MiniMessage.miniMessage().deserialize(message))
    }
}