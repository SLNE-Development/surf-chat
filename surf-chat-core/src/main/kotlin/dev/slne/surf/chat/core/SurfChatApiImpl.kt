package dev.slne.surf.chat.core

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.core.service.ignoreService
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

@AutoService(SurfChatApi::class)
class SurfChatApiImpl : SurfChatApi {
    override suspend fun logMessage(data: MessageData) {
        historyService.logMessage(data)
    }

    override fun getCachedIgnoreList(uuid: UUID): List<IgnoreListEntry> {
        return ignoreService.getCachedIgnoreList(uuid)
    }

    override suspend fun getIgnoreList(uuid: UUID): List<IgnoreListEntry> {
        return ignoreService.loadIgnoreList(uuid)
    }

    override suspend fun lookupHistory(filter: HistoryFilter): ObjectList<HistoryEntry> {
        return historyService.findHistoryEntry(filter)
    }
}