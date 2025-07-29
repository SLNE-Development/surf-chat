package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.chat.core.service.databaseService
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
    }

    override fun onDisable() {
        databaseService.closeConnection()
    }
}