package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.BlacklistService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.util.Services.Fallback

@AutoService(BlacklistService::class)
class BukkitBlacklistService(): BlacklistService, Fallback {
    private val blackList: ObjectSet<String> = ObjectArraySet()

    override fun isBlackListed(word: String): Boolean {
        return blackList.contains(word)
    }

    override fun hasBlackListed(message: Component): Boolean {
        message.children().all {
            return@all !isBlackListed(PlainTextComponentSerializer.plainText().serialize(it))
        }

        return false
    }

    override fun addToBlacklist(name: String): Boolean {
        return blackList.add(name)
    }

    override fun removeFromBlacklist(name: String): Boolean {
        return blackList.remove(name)
    }
}