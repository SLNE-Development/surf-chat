package dev.slne.surf.chat.bukkit.listener

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.time.Duration.Companion.minutes

class AsyncChatListener : Listener {
    private val pingPattern = Regex("^@(all|a|here|everyone)\\b\\s*", RegexOption.IGNORE_CASE)
    private val hexRegex = Regex("&#[A-Fa-f0-9]{6}")
    private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")
    private val itemRegex = Regex("\\[(?i)item]")
    private val nameRegexCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes)
        .build<String, Regex> {
            Regex("(?<!\\w)@?$it(?!\\w)")
        }

    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val player = event.player
        val message = event.message()


        event.renderer { _, displayName, _, viewerAudience ->
            message
        }
    }
}