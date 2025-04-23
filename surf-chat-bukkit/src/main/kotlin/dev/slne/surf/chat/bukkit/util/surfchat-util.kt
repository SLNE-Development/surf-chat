package dev.slne.surf.chat.bukkit.util

import com.google.gson.Gson
import dev.slne.surf.chat.bukkit.plugin
import org.bukkit.Bukkit

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

val pluginConfig = plugin.config
val gson = Gson()
val serverPlayers = Bukkit.getOnlinePlayers()

val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(ZoneId.of("Europe/Berlin"))

fun formatTime(millis: Long): String {
    return timeFormatter.format(Instant.ofEpochMilli(millis))
}

fun debug(message: Any) {
    println("[SurfChat-Debug] $message")
}