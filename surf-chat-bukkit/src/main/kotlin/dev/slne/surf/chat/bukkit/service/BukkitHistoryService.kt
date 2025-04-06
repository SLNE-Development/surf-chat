package dev.slne.surf.chat.bukkit.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.model.BukkitHistoryEntry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.core.service.databaseService
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.*

class BukkitHistoryService(
    override val historyCache: Cache<UUID, Component>
): HistoryService {
    override suspend fun write(user: UUID, type: ChatMessageType, message: Component) {
        databaseService.insertHistoryEntry(user, BukkitHistoryEntry(
            message, System.currentTimeMillis(), user, type.toString(), UUID.randomUUID()
        ))
    }

    override suspend fun getHistory(user: ChatUserModel): ObjectList<HistoryEntryModel> {
        return databaseService.loadHistory(user.uuid)
    }

    override fun logCaching(uuid: UUID, message: Component) {
        historyCache.put(uuid, message)
    }

    override fun deleteMessage(id: UUID) {
        historyCache.invalidate(id)
    }

    override fun resendMessages(last: Int) {
        this.clearChat()

        val history = historyCache.asMap().values.toList().stream().limit(last.toLong())

        history.forEach { message ->
            Bukkit.getOnlinePlayers().forEach { player ->
                player.sendMessage(message)
            }
        }
    }

    override fun clearChat() {
        historyCache.invalidateAll()
        Bukkit.getOnlinePlayers().forEach { player ->
            repeat(50) {
                player.sendMessage(Component.empty())
            }
        }
    }
}