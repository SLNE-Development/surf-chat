package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage

interface DenylistActionService {
    suspend fun addAction(action: DenylistAction)
    suspend fun removeAction(action: DenylistAction)
    suspend fun fetchActions()

    fun addLocalAction(action: DenylistAction): Boolean
    fun removeLocalAction(action: DenylistAction): Boolean
    fun getLocalAction(name: String): DenylistAction?
    fun listLocalActions(): ObjectSet<DenylistAction>

    suspend fun makeAction(action: DenylistAction, message: SignedMessage, sender: User)

    companion object {
        val INSTANCE = requiredService<DenylistActionService>()
    }
}

val denylistActionService get() = DenylistActionService.INSTANCE