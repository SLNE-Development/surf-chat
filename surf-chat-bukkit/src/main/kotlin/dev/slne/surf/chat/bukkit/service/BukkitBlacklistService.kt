package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.BlacklistService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services.Fallback

@AutoService(BlacklistService::class)
class BukkitBlacklistService(): BlacklistService, Fallback {
    private val blackList: ObjectSet<String> = ObjectArraySet()

    override fun isBlackListed(name: String): Boolean {
        return blackList.contains(name)
    }

    override fun addToBlacklist(name: String): Boolean {
        return blackList.add(name)
    }

    override fun removeFromBlacklist(name: String): Boolean {
        return blackList.remove(name)
    }
}