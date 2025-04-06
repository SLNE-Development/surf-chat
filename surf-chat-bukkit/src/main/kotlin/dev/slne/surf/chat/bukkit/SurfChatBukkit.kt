package dev.slne.surf.chat.bukkit

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin

import com.google.gson.Gson

import dev.slne.surf.chat.api.model.ChatFormatModel
import dev.slne.surf.chat.api.model.MessageValidatorModel
import dev.slne.surf.chat.bukkit.model.BukkitChatFormat
import dev.slne.surf.chat.bukkit.model.BukkitMessageValidator

import org.bukkit.plugin.java.JavaPlugin

class SurfChatBukkit(): SuspendingJavaPlugin() {
    val chatFormat: ChatFormatModel = BukkitChatFormat()
    val messageValidator: MessageValidatorModel = BukkitMessageValidator()
}

val plugin = JavaPlugin.getPlugin(SurfChatBukkit::class.java)
val gson = Gson()