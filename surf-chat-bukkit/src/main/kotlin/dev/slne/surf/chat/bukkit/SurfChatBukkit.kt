package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.jorel.commandapi.CommandAPI
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.model.ChatFormatModel
import dev.slne.surf.chat.api.model.MessageValidatorModel
import dev.slne.surf.chat.bukkit.command.PrivateMessageCommand
import dev.slne.surf.chat.bukkit.command.PrivateMessageSpyCommand
import dev.slne.surf.chat.bukkit.command.ReplyCommand
import dev.slne.surf.chat.bukkit.command.TeamChatCommand
import dev.slne.surf.chat.bukkit.command.denylist.DenyListCommand
import dev.slne.surf.chat.bukkit.command.channel.ChannelCommand
import dev.slne.surf.chat.bukkit.command.ignore.IgnoreCommand
import dev.slne.surf.chat.bukkit.command.ignore.IgnoreListCommand
import dev.slne.surf.chat.bukkit.command.surfchat.SurfChatCommand
import dev.slne.surf.chat.bukkit.command.toggle.ToggleCommand
import dev.slne.surf.chat.bukkit.extension.LuckPermsExtension
import dev.slne.surf.chat.bukkit.listener.BukkitChatListener
import dev.slne.surf.chat.bukkit.listener.BukkitConnectionListener
import dev.slne.surf.chat.bukkit.model.BukkitChatFormat
import dev.slne.surf.chat.bukkit.model.BukkitMessageValidator
import dev.slne.surf.chat.bukkit.service.BukkitMessagingReceiverService
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.serverPlayers
import dev.slne.surf.chat.core.service.*
import dev.slne.surf.chat.core.service.messaging.messagingSenderService
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

class SurfChatBukkit() : SuspendingJavaPlugin() {
    val chatFormat: ChatFormatModel = BukkitChatFormat()
    val messageValidator: MessageValidatorModel = BukkitMessageValidator()

    override suspend fun onEnableAsync() {

        /**
         * Register all commands.
         */

        CommandAPI.unregister("msg")
        CommandAPI.unregister("tell")
        CommandAPI.unregister("w")

        ChannelCommand("channel").register()
        SurfChatCommand("surfchat").register()
        IgnoreCommand("ignore").register()
        PrivateMessageCommand("msg").register()
        ReplyCommand("reply").register()
        ToggleCommand("toggle").register()
        TeamChatCommand("teamchat").register()
        DenyListCommand("denylist").register()
        PrivateMessageSpyCommand("pmspy").register()
        IgnoreListCommand("ignorelist").register()

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
        Bukkit.getMessenger()
            .registerOutgoingPluginChannel(this, SurfChatApi.MESSAGING_CHANNEL_IDENTIFIER)
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, SurfChatApi.TEAM_CHAT_IDENTIFIER)

        /**
         * Handle & start services.
         */

        plugin.saveDefaultConfig()

        databaseService.connect()
        denylistService.fetch()
        chatMotdService.loadMotd()
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
