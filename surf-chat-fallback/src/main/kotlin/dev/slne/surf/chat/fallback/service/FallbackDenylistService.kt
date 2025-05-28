package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.core.service.DenylistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.fallback.model.entry.FallbackDenylistEntry
import dev.slne.surf.chat.fallback.util.toPlainText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services

@AutoService(DenylistService::class)
class FallbackDenylistService : DenylistService, Services.Fallback {
    var denyList: ObjectSet<DenyListEntry>? = null

    override fun isDenylisted(word: String): Boolean {
        if(denyList == null) {
            return false
        }

        val list = denyList ?: return false

        return list.any { it.word.equals(word, ignoreCase = true) }
    }

    override fun hasDenyListed(message: Component): Boolean {
        if (denyList == null) {
            return false
        }

        val list = denyList ?: return false

        return list.any { entry ->
            message.children().any { it.toPlainText().contains(entry.word, ignoreCase = true) }
        }
    }

    override suspend fun addToDenylist(
        word: String,
        reason: String,
        addedAt: Long,
        addedBy: String
    ): Boolean {
        return databaseService.addToDenylist(FallbackDenylistEntry(
            word = word,
            reason = reason,
            addedAt = addedAt,
            addedBy = addedBy
        ))
    }

    override suspend fun removeFromDenylist(word: String): Boolean {
        return databaseService.removeFromDenylist(word)
    }

    override suspend fun fetch() {
        denyList = databaseService.loadDenyList()
    }
}