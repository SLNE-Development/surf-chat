package dev.slne.surf.chat.core.client.denylist

import dev.slne.surf.chat.api.ChatContextHolder
import dev.slne.surf.chat.api.InternalChatApi
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.core.common.util.SyncValues
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Service

@Service
class DenylistActionService {
    fun addAction(entry: DenylistAction) = SyncValues.denylistActions.add(entry)
    fun removeAction(entry: DenylistAction) =
        SyncValues.denylistActions.removeIf { it.name == entry.name }

    fun hasAction(name: String) = SyncValues.denylistActions.any { it.name == name }
    fun getActions() = SyncValues.denylistActions
    fun clearActions() = SyncValues.denylistActions.clear()
    fun getAction(name: String) =
        SyncValues.denylistActions.firstOrNull { it.name == name }
}

@OptIn(InternalChatApi::class)
val denylistActionService get() = ChatContextHolder.instance.context.getBean<DenylistActionService>()