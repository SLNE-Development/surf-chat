package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.chat.api.model.ChatFormat
import dev.slne.surf.chat.bukkit.listener.BukkitChatListener
import dev.slne.surf.chat.bukkit.model.BukkitChatFormat
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.plugin.java.JavaPlugin

class PaperMain() : SuspendingJavaPlugin() {
    val chatFormat: ChatFormat = BukkitChatFormat()

    override fun onEnable() {
        BukkitChatListener().register()
    }
}

val plugin = JavaPlugin.getPlugin(PaperMain::class.java)
