package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.core.service.DenylistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.Services.Fallback

@AutoService(DenylistService::class)
class BukkitDenylistService() : DenylistService, Fallback {
    private var denylist: ObjectSet<String> = mutableObjectSetOf()

    override fun isDenylisted(word: String): Boolean {
        return denylist.contains(word)
    }

    override fun hasDenyListed(message: Component): Boolean {
        val string = PlainTextComponentSerializer.plainText().serialize(message)

        string.split(" ").forEach { word ->
            if (denylist.any { it.equals(word, ignoreCase = true) }) {
                return true
            }
        }
        return false
    }

    override suspend fun addToDenylist(word: DenyListEntry): Boolean {
        return databaseService.addToDenylist(word)
    }

    override suspend fun removeFromDenylist(word: String): Boolean {
        return databaseService.removeFromDenylist(word)
    }

    override suspend fun fetch() {
        this.denylist = databaseService.loadDenyList().map { it.word }.toObjectSet()
    }
}