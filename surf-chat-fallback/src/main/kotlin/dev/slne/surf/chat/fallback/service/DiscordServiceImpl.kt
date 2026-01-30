package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.service.DiscordService
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import dev.slne.surf.surfapi.core.api.util.logger
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.kyori.adventure.util.Services
import java.time.format.DateTimeFormatter
import java.util.*

@AutoService(DiscordService::class)
class DiscordServiceImpl : DiscordService, Services.Fallback {
    private val log = logger()
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
            })
        }
    }

    @Serializable
    private data class EmbedField(
        val name: String,
        val value: String,
        val inline: Boolean = false
    )

    @Serializable
    private data class Embed(
        val title: String,
        val color: Int,
        val fields: List<EmbedField>
    )

    @Serializable
    private data class WebhookPayload(
        val embeds: List<Embed>
    )

    override suspend fun sendCommunityBanNotification(
        url: String,
        userUuid: UUID,
        denylistEntry: DenylistEntry
    ) {
        val userName = PlayerLookupService.getUsername(userUuid) ?: "Unknown User"
        val addedByName = denylistEntry.addedBy?.let { PlayerLookupService.getUsername(it) } ?: "Unknown Moderator"
        val timestamp = denylistEntry.addedAt.format(DateTimeFormatter.RFC_1123_DATE_TIME)

        val embed = Embed(
            title = "Community Ban",
            color = 0xFF0000,
            fields = listOf(
                EmbedField("User", "$userName ($userUuid)", inline = true),
                EmbedField("Reason", denylistEntry.reason),
                EmbedField("Word Triggered", denylistEntry.word, inline = true),
                EmbedField("Added By", addedByName, inline = true),
                EmbedField("Action", denylistEntry.action.name, inline = true),
                EmbedField("Timestamp", timestamp)
            )
        )

        val payload = WebhookPayload(listOf(embed))

        try {
            client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        } catch (e: Exception) {
            log.atSevere()
                .withCause(e)
                .log("Error sending Discord webhook: ${e.message}")
        }
    }
}