package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.model.ChatFormat
import dev.slne.surf.chat.api.model.MessageValidator
import dev.slne.surf.chat.bukkit.command.CommandManager
import dev.slne.surf.chat.bukkit.extension.LuckPermsExtension
import dev.slne.surf.chat.bukkit.listener.BukkitChatListener
import dev.slne.surf.chat.bukkit.listener.BukkitConnectionListener
import dev.slne.surf.chat.bukkit.model.BukkitChatFormat
import dev.slne.surf.chat.bukkit.model.BukkitMessageValidator
import dev.slne.surf.chat.bukkit.service.BukkitMessagingReceiverService
import dev.slne.surf.chat.core.service.*
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import dev.slne.surf.surfapi.bukkit.api.event.register
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

class SurfChatBukkit() : SuspendingJavaPlugin() {
    val chatFormat: ChatFormat = BukkitChatFormat()
    val messageValidator: MessageValidator = BukkitMessageValidator()

    override suspend fun onEnableAsync() {

        /**
         * Register all commands.
         */

        CommandManager.registerAll()

        /**
         * Register all listeners.
         */

        BukkitChatListener().register()
        BukkitConnectionListener().register()

        Bukkit.getMessenger().registerIncomingPluginChannel(
            this,
            SurfChatApi.MESSAGING_CHANNEL_IDENTIFIER,
            BukkitMessagingReceiverService()
        )
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, SurfChatApi.MESSAGING_CHANNEL_IDENTIFIER)
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, SurfChatApi.TEAM_CHAT_IDENTIFIER)

        /**
         * Handle & start services.
         */

        plugin.saveDefaultConfig()

        databaseService.connect()
        denylistService.fetch()
        chatMotdService.loadMotd()
        filterService.loadDomains()
        filterService.loadMessageLimit()
        chatFormat.loadServer()
        messagingSenderService.loadServers()
        connectionService.loadMessages()
        LuckPermsExtension.loadApi()
    }

    override suspend fun onDisableAsync() {
        val ms = measureTimeMillis {
            filterService.saveMessageLimit()
            chatMotdService.saveMotd()
            runBlocking {
                databaseService.saveAll()
            }
        }

        logger.info { "Successfully disabled in ${ms}ms!" }
    }
}

val plugin = JavaPlugin.getPlugin(SurfChatBukkit::class.java)
