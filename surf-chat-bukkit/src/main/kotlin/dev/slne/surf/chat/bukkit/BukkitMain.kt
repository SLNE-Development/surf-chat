package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.config.ChatMotdConfigProvider
import dev.slne.surf.chat.bukkit.config.ConnectionMessageConfigProvider
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.denylistService
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

class BukkitMain : SuspendingJavaPlugin() {
    var serverName = Optional.empty<String>()

    override fun onLoad() {
        databaseService.establishConnection(plugin.dataPath)
        databaseService.createTables()
    }

    override fun onEnable() {
        BukkitCommandManager.registerCommands()
        BukkitListenerManager.registerBukkitListeners()
        BukkitListenerManager.registerPacketListeners()

        launch {
            denylistService.fetch()
        }
    }

    override fun onDisable() {
        databaseService.closeConnection()
    }

    val connectionMessageConfig = ConnectionMessageConfigProvider()
    val chatMotdConfig = ChatMotdConfigProvider()
}