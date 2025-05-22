package dev.slne.surf.chat.bukkit.service

import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChatUser
import dev.slne.surf.chat.api.model.HistoryEntry
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.bukkit.model.BukkitHistoryEntry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.Bukkit
import java.util.*

@AutoService(HistoryService::class)
class BukkitHistoryService() : HistoryService, Fallback {
    private val messageCache: Object2ObjectMap<UUID, SignedMessage.Signature> =
        mutableObject2ObjectMapOf()

    override suspend fun write(
        user: UUID,
        type: MessageType,
        message: Component,
        messageID: UUID
    ) {
        databaseService.insertHistoryEntry(
            user, BukkitHistoryEntry(
                message = PlainTextComponentSerializer.plainText().serialize(message),
                timestamp = System.currentTimeMillis(),
                userUuid = user,
                type = type,
                entryUuid = messageID,
                server = BukkitMessagingSenderService.getCurrentServer()
            )
        )
    }

    override suspend fun getHistory(user: ChatUser): ObjectList<HistoryEntry> {
        return databaseService.loadHistory(user.uuid)
    }

    override fun getMessageIds(): ObjectSet<UUID> {
        return messageCache.keys
    }

    override fun logCaching(message: SignedMessage.Signature, messageID: UUID) {
        this.messageCache[messageID] = message
    }

    override fun deleteMessage(name: String, messageID: UUID): Boolean {
        val signedMessage = messageCache[messageID] ?: return false

        Bukkit.getServer().deleteMessage(signedMessage)
        messageCache.remove(messageID)

        plugin.launch {
            databaseService.markMessageDeleted(name, messageID)
        }

        return true
    }


    override fun clearChat() {
        forEachPlayer { player ->
            repeat(100) {
                player.sendMessage(Component.empty())
            }
        }
    }
}