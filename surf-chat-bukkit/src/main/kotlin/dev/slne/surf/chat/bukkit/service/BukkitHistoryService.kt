package dev.slne.surf.chat.bukkit.service

import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.SurfChatBukkit
import dev.slne.surf.chat.bukkit.model.BukkitHistoryEntry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.util.HistoryEntry
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCopiesToClipboard
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.Bukkit
import java.util.*

@AutoService(HistoryService::class)
class BukkitHistoryService() : HistoryService, Fallback {
    private val messageCache: Object2ObjectMap<UUID, HistoryEntry> =
        mutableObject2ObjectMapOf()

    override suspend fun write(
        user: UUID,
        type: ChatMessageType,
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

    override suspend fun getHistory(user: ChatUserModel): ObjectList<HistoryEntryModel> {
        return databaseService.loadHistory(user.uuid)
    }

    override fun getMessageIds(): ObjectSet<UUID> {
        return messageCache.keys
    }

    override fun logCaching(message: HistoryEntry, messageID: UUID) {
        this.messageCache[messageID] = message
    }

    override fun deleteMessage(name: String, messageID: UUID): Boolean {
        val entry = messageCache[messageID] ?: return false

        Bukkit.getServer().deleteMessage(entry.signature)
        messageCache.remove(messageID)

        plugin.launch {
            databaseService.markMessageDeleted(name, messageID)
        }

        forEachPlayer {
            if(it.hasPermission(ChatPermissionRegistry.COMMAND_TEAMCHAT)) {
                it.sendPrefixed {
                    variableValue(name)
                    info(" hat eine Nachricht gelöscht: ")
                    variableValue(entry.message)
                    hoverEvent(buildText {
                        info("Klicke, um die Nachrichten-ID zu kopieren.")
                    })
                    clickCopiesToClipboard(messageID.toString())
                }
            }
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