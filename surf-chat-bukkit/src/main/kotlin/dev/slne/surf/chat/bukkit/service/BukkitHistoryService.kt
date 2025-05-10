package dev.slne.surf.chat.bukkit.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.model.BukkitHistoryEntry
import dev.slne.surf.chat.api.util.history.HistoryPair
import dev.slne.surf.chat.api.util.history.LoggedMessage
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.player
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.object2ObjectMapOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.Bukkit
import java.util.*

@AutoService(HistoryService::class)
class BukkitHistoryService(): HistoryService, Fallback {
    private val historyCache = Caffeine.newBuilder()
        .maximumSize(1000)
        .build<UUID, Object2ObjectMap<HistoryPair, LoggedMessage>>()

    private val messageCache: Object2ObjectMap<UUID, SignedMessage.Signature> = Object2ObjectOpenHashMap()

    override suspend fun write(user: UUID, type: ChatMessageType, message: Component, messageID: UUID) {
        databaseService.insertHistoryEntry(user, BukkitHistoryEntry(
            message = PlainTextComponentSerializer.plainText().serialize(message),
            timestamp = System.currentTimeMillis(),
            uuid = user,
            type = type.toString(),
            id = messageID,
            server = BukkitMessagingSenderService.getCurrentServer()
        ))
    }

    override suspend fun getHistory(user: ChatUserModel): ObjectList<HistoryEntryModel> {
        return databaseService.loadHistory(user.uuid)
    }

    override fun logCaching(message: SignedMessage.Signature, messageID: UUID) {
        this.messageCache[messageID] = message
    }

    override fun deleteMessage(name: String, messageID: UUID) {
        val signedMessage = messageCache[messageID] ?: return

        Bukkit.getServer().deleteMessage(signedMessage)
        messageCache.remove(messageID)

        plugin.launch {
            databaseService.markMessageDeleted(name, messageID)
        }
    }


    override fun clearChat() {
        forEachPlayer { player ->
            repeat(100) {
                player.sendMessage(Component.empty())
            }
        }
    }
}