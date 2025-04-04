package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.surfapi.core.api.util.requiredService

import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component

interface HistoryService {
    fun write(user: ChatUserModel, message: Component)
    fun getHistory(user: ChatUserModel): ObjectList<HistoryEntryModel>

    companion object {
        val INSTANCE = requiredService<HistoryService>()
    }
}

val historyService get() = HistoryService.INSTANCE