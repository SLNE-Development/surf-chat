package dev.slne.surf.chat.bukkit.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.model.BukkitHistoryEntry
import dev.slne.surf.chat.api.util.history.HistoryPair
import dev.slne.surf.chat.api.util.history.LoggedMessage
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import java.util.*

@AutoService(HistoryService::class)
class BukkitHistoryService(): HistoryService, Fallback {
    private val historyCache = Caffeine.newBuilder()
        .maximumSize(1000)
        .build<UUID, Object2ObjectMap<HistoryPair, LoggedMessage>>()

    override suspend fun write(user: UUID, type: ChatMessageType, message: Component) {
        databaseService.insertHistoryEntry(user, BukkitHistoryEntry(
            PlainTextComponentSerializer.plainText().serialize(message), System.currentTimeMillis(), user, type.toString(), UUID.randomUUID()
        ))
    }

    override suspend fun getHistory(user: ChatUserModel): ObjectList<HistoryEntryModel> {
        return databaseService.loadHistory(user.uuid)
    }

    override fun logCaching(player: UUID, message: LoggedMessage, messageID: UUID) {
        val chatHistory = historyCache.get(player) { mutableObject2ObjectMapOf() }

        val timestamp = System.currentTimeMillis() / 1000
        chatHistory[HistoryPair(messageID, timestamp)] = message
    }

    override fun deleteMessage(player: UUID, messageID: UUID) {
        val chatHistory = historyCache.getIfPresent(player) ?: return
        val key = chatHistory.keys.find { it.messageID == messageID }

        if (key != null) {
            chatHistory.remove(key)
        }
    }

    override fun resendMessages(player: UUID) {
        val chatHistory = historyCache.getIfPresent(player) ?: return

        forEachPlayer { online ->
            repeat(100) {
                online.sendMessage(Component.empty())
            }
        }

        chatHistory.object2ObjectEntrySet()
            .sortedBy { it.key.sendTime }
            .forEach { (_, value) ->
                forEachPlayer { player ->
                    player.sendMessage(value.message)
                }
            }
    }

    override fun clearChat() {
        historyCache.invalidateAll()

        forEachPlayer { player ->
            repeat(100) {
                player.sendMessage(Component.empty())
            }
        }
    }
}