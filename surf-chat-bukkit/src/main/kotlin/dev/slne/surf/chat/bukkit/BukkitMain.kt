package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.bukkit.config.ChatMotdConfigProvider
import dev.slne.surf.chat.bukkit.config.ChatServerProviderConfig
import dev.slne.surf.chat.bukkit.config.ConnectionMessageConfigProvider
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.functionalityService
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

class BukkitMain : SuspendingJavaPlugin() {
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
            functionalityService.fetch(server)
        }
    }

    override fun onDisable() {
        databaseService.closeConnection()
    }

    val connectionMessageConfig = ConnectionMessageConfigProvider()

    val chatMotdConfig = ChatMotdConfigProvider()
    val chatServerConfig = ChatServerProviderConfig()
    var server = ChatServer.of(
        plugin.chatServerConfig.config().internalName,
        plugin.chatServerConfig.config().displayName
    )
}