package dev.slne.surf.chat.bukkit.service

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.model.HistoryEntryModel
import dev.slne.surf.chat.core.service.HistoryService
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component

class BukkitHistoryService(): HistoryService {
    override fun write(user: ChatUserModel, message: Component) {
        TODO("Not yet implemented")
    }

    override fun getHistory(user: ChatUserModel): ObjectList<HistoryEntryModel> {
        TODO("Not yet implemented")
    }
}