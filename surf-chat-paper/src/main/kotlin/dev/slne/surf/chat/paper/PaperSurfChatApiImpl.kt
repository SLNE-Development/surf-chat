package dev.slne.surf.chat.paper

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.core.common.service.HistoryService
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.paper.command.direct.DirectMessageAccess
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import java.util.*

@AutoService(SurfChatApi::class)
class PaperSurfChatApiImpl : SurfChatApi, Services.Fallback {
    override suspend fun logMessage(data: MessageData) {
        HistoryService.logMessage(data)
    }

    override fun registerChatProcessor(processor: PreChatProcessor) {
        chatProcessorRegistry.register(processor)
    }

    override fun registerChatProcessor(processor: PostChatProcessor) {
        chatProcessorRegistry.register(processor)
    }

    override fun getCachedIgnoreList(uuid: UUID): List<IgnoreListEntry> = IgnoreService.getCachedIgnoreList(uuid)

    override suspend fun getIgnoreList(uuid: UUID): List<IgnoreListEntry> = IgnoreService.loadIgnoreList(uuid)

    override suspend fun lookupHistory(filter: HistoryFilter): ObjectList<HistoryEntry> =
        HistoryService.findHistoryEntry(filter)

    override suspend fun sendSignedMessage(
        sender: UUID,
        message: SignedMessage,
        targetUuids: Collection<UUID>,
        outgoingFormatter: (suspend (MessageData) -> Component)?,
        incomingFormatter: (suspend (MessageData) -> Component)?,
    ) {
        val player = Bukkit.getPlayer(sender) ?: return
        DirectMessageAccess.sendMessage(player, message, targetUuids, outgoingFormatter, incomingFormatter)
    }

}