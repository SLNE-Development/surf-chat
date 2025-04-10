package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.google.gson.Gson
import dev.jorel.commandapi.CommandAPI

import dev.slne.surf.chat.api.model.ChatFormatModel
import dev.slne.surf.chat.api.model.MessageValidatorModel
import dev.slne.surf.chat.bukkit.command.*
import dev.slne.surf.chat.bukkit.extension.LuckPermsExtension
import dev.slne.surf.chat.bukkit.listener.BukkitChatListener
import dev.slne.surf.chat.bukkit.listener.BukkitConnectionListener
import dev.slne.surf.chat.bukkit.model.BukkitChatFormat
import dev.slne.surf.chat.bukkit.model.BukkitMessageValidator
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.bukkit.command.channel.ChannelCommand
import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet

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
        TeamChatCommand("teamchat").register()

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
        chatMotdService.loadMotd()
        LuckPermsExtension.loadApi()
    }

    override suspend fun onDisableAsync() {
        chatMotdService.saveMotd()
    }

    fun getTeamMembers(): ObjectSet<Player> = Bukkit.getOnlinePlayers().filter { it.hasPermission("surf.chat.command.teamchat") }.toObjectSet()
}

val plugin = JavaPlugin.getPlugin(SurfChatBukkit::class.java)
val gson = Gson()