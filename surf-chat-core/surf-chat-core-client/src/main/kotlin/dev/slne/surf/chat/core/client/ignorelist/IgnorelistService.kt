@file:OptIn(InternalChatApi::class)

package dev.slne.surf.chat.core.client.ignorelist

import dev.slne.surf.chat.api.ChatContextHolder
import dev.slne.surf.chat.api.ChatUuid
import dev.slne.surf.chat.api.InternalChatApi
import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.core.common.util.SyncValues
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Service

@Service
class IgnorelistService {
    fun getIgnoreList(uuid: ChatUuid) =
        SyncValues.ignoreList.firstOrNull { it.first == uuid }?.second ?: emptySet()

    fun addToIgnoreList(ignorelistEntry: IgnoreListEntry) {
        val entry = SyncValues.ignoreList.firstOrNull { it.first == ignorelistEntry.user }

        if (entry != null) {
            entry.second.add(ignorelistEntry)

            SyncValues.ignoreList.removeIf { it.first == ignorelistEntry.user }
            SyncValues.ignoreList.add(entry.first to entry.second)
        } else {
            SyncValues.ignoreList.add(ignorelistEntry.user to mutableSetOf(ignorelistEntry))
        }
    }

    fun removeFromIgnoreList(user: ChatUuid, target: ChatUuid) {
        val entry = SyncValues.ignoreList.firstOrNull { it.first == user }

        entry?.second?.removeIf { it.target == target }

        if (entry != null) {
            SyncValues.ignoreList.removeIf { it.first == user }
            SyncValues.ignoreList.add(entry.first to entry.second)
        }
    }

    fun isIgnoring(user: ChatUuid, target: ChatUuid): Boolean {
        val entry = SyncValues.ignoreList.firstOrNull { it.first == user }

        return entry?.second?.any { it.target == target } ?: false
    }
}

val ignorelistService get() = ChatContextHolder.instance.context.getBean<IgnorelistService>()