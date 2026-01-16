package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.SpyService
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SpyService::class)
class FallbackSpyService : SpyService, Services.Fallback {
    val privateMessageSpies = mutableObject2ObjectMapOf<UUID, ObjectList<UUID>>()

    override fun getPrivateMessageSpies(player: UUID) =
        privateMessageSpies.get(player) ?: mutableObjectListOf()


    override fun addPrivateMessageSpy(player: UUID, target: UUID) =
        privateMessageSpies.computeIfAbsent(target) { mutableObjectListOf() }.add(player)

    override fun removePrivateMessageSpy(player: UUID, target: UUID) =
        privateMessageSpies[target]?.remove(player) ?: false

    override fun hasPrivateMessageSpies(player: UUID) =
        privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true

    override fun isPrivateMessageSpying(player: UUID) =
        privateMessageSpies.containsKey(player) && privateMessageSpies[player]?.isNotEmpty() == true

    override fun clearPrivateMessageSpies(player: UUID) {
        privateMessageSpies.values.forEach { it.remove(player) }
        privateMessageSpies.keys.removeIf { privateMessageSpies[it]?.isEmpty() == true }
    }

    override fun cleanup(player: UUID) {
        this.clearPrivateMessageSpies(player)
    }
}