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
        SyncValues.ignoreList.firstOrNull { it.user == uuid }?.entries ?: emptySet()

    fun addToIgnoreList(ignorelistEntry: IgnoreListEntry) {
        val entry = SyncValues.ignoreList.firstOrNull { it.user == ignorelistEntry.user }

        if (entry != null) {
            entry.entries.add(ignorelistEntry)

            SyncValues.ignoreList.removeIf { it.user == ignorelistEntry.user }
            SyncValues.ignoreList.add(SyncValues.Ignorelist(entry.user, entry.entries))
        } else {
            SyncValues.ignoreList.add(
                SyncValues.Ignorelist(
                    ignorelistEntry.user,
                    mutableSetOf(ignorelistEntry)
                )
            )
        }
    }

    fun removeFromIgnoreList(user: ChatUuid, target: ChatUuid) {
        val entry = SyncValues.ignoreList.firstOrNull { it.user == user }

        entry?.entries?.removeIf { it.user == target }

        if (entry != null) {
            SyncValues.ignoreList.removeIf { it.user == user }
            SyncValues.ignoreList.add(SyncValues.Ignorelist(entry.user, entry.entries))
        }
    }

    fun isIgnoring(user: ChatUuid, target: ChatUuid): Boolean {
        val entry = SyncValues.ignoreList.firstOrNull { it.user == user }

        return entry?.entries?.any { it.target == target } ?: false
    }
}

val ignorelistService get() = ChatContextHolder.instance.context.getBean<IgnorelistService>()