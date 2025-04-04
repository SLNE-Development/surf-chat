package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.model.ChatUserModel
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

class BukkitChatUser (
    override val uuid: UUID,
    override val name: String = "Unknown",
    override val ignoreList: ObjectSet<UUID> = ObjectArraySet(),
    override var pmToggled: Boolean = false
): ChatUserModel {
    override fun isIgnoring(target: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun ignore(target: UUID) {
        TODO("Not yet implemented")
    }

    override fun unIgnore(target: UUID) {
        TODO("Not yet implemented")
    }

    override fun toggleIgnore(target: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasOpenPms(): Boolean {
        TODO("Not yet implemented")
    }

    override fun togglePm(): Boolean {
        TODO("Not yet implemented")
    }

    override fun moveToChannel(channel: ChannelModel) {
        TODO("Not yet implemented")
    }
}