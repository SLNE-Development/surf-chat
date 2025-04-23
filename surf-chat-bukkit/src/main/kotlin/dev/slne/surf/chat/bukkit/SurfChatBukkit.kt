package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin

import dev.jorel.commandapi.CommandAPI

import dev.slne.surf.chat.api.model.ChatFormatModel
import dev.slne.surf.chat.api.model.MessageValidatorModel
import dev.slne.surf.chat.bukkit.command.*
import dev.slne.surf.chat.bukkit.command.blacklist.BlackListCommand
import dev.slne.surf.chat.bukkit.extension.LuckPermsExtension
import dev.slne.surf.chat.bukkit.listener.BukkitChatListener
import dev.slne.surf.chat.bukkit.listener.BukkitConnectionListener
import dev.slne.surf.chat.bukkit.model.BukkitChatFormat
import dev.slne.surf.chat.bukkit.model.BukkitMessageValidator
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.bukkit.command.channel.ChannelCommand
import dev.slne.surf.chat.bukkit.command.surfchat.SurfChatCommand
import dev.slne.surf.chat.bukkit.command.toggle.TogglePmCommand
import dev.slne.surf.chat.bukkit.command.toggle.ToggleSoundCommand
import dev.slne.surf.chat.bukkit.util.serverPlayers
import dev.slne.surf.chat.core.service.blacklistService
import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.chat.core.service.filterService
import dev.slne.surf.surfapi.core.api.util.toObjectSet

import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.runBlocking

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class SurfChatBukkit(): SuspendingJavaPlugin() {
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
        TogglePmCommand("togglepm").register()
        ToggleSoundCommand("togglesound").register()
        TeamChatCommand("teamchat").register()
        BlackListCommand("blacklist").register()

        /**
         * Register all listeners.
         */

        Bukkit.getPluginManager().registerEvents(BukkitChatListener(), this)
        Bukkit.getPluginManager().registerEvents(BukkitConnectionListener(), this)

        /**
         * Handle & start services.
         */

        plugin.saveDefaultConfig()

        databaseService.connect()
        blacklistService.fetch()
        chatMotdService.loadMotd()
        filterService.loadMessageLimit()
        chatFormat.loadServer()
        LuckPermsExtension.loadApi()
    }

    override fun onDisable() {
        runBlocking {
            chatMotdService.saveMotd()
            filterService.saveMessageLimit()
            databaseService.saveAll()
        }
    }

    fun getTeamMembers(): ObjectSet<Player> = serverPlayers.filter { it.hasPermission("surf.chat.command.teamchat") }.toObjectSet()
}

val plugin = JavaPlugin.getPlugin(SurfChatBukkit::class.java)
