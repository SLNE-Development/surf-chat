package dev.slne.surf.chat.core.client.denylist

import dev.slne.surf.chat.api.ChatContextHolder
import dev.slne.surf.chat.api.InternalChatApi
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.common.util.SyncValues
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Service

@Service
class DenylistService {
    fun addEntry(entry: DenylistEntry) = SyncValues.denylistEntries.add(entry)
    fun removeEntry(entry: DenylistEntry) =
        SyncValues.denylistEntries.removeIf { it.word == entry.word }

    fun hasEntry(word: String) = SyncValues.denylistEntries.any { it.word == word }
    fun getEntries() = SyncValues.denylistEntries
}

@OptIn(InternalChatApi::class)
val denylistService get() = ChatContextHolder.instance.context.getBean<DenylistService>()