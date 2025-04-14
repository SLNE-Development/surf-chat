package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.model.BlacklistWordModel
import dev.slne.surf.chat.core.service.BlacklistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.Services.Fallback

@AutoService(BlacklistService::class)
class BukkitBlacklistService(): BlacklistService, Fallback {
    private var blackList: ObjectSet<String> = ObjectArraySet()

    override fun isBlackListed(word: String): Boolean {
        return blackList.contains(word)
    }

    override fun hasBlackListed(message: Component): Boolean {
        message.children().all {
            return@all !isBlackListed(PlainTextComponentSerializer.plainText().serialize(it))
        }

        return false
    }

    override suspend fun addToBlacklist(word: BlacklistWordModel): Boolean {
        return databaseService.addToBlacklist(word)
    }

    override suspend fun removeFromBlacklist(word: String): Boolean {
        return databaseService.removeFromBlacklist(word)
    }

    override suspend fun fetch() {
        this.blackList = databaseService.loadBlacklist().map { it.word }.toObjectSet()
    }
}