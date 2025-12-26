package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import com.google.gson.Gson
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.DiscordService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.util.Services
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AutoService(DiscordService::class)
class FallbackDiscordService : DiscordService, Services.Fallback {
    private val client = OkHttpClient()
    private val gson = Gson()

    private data class EmbedField(
        val name: String,
        val value: String,
        val inline: Boolean = false
    )

    private data class Embed(
        val title: String,
        val color: Int,
        val fields: List<EmbedField>
    )

    private data class WebhookPayload(
        val embeds: List<Embed>
    )

    override suspend fun sendCommunityBanNotification(
        url: String,
        user: User,
        denylistEntry: DenylistEntry
    ) = withContext(Dispatchers.IO) {
        val timestamp = Instant.ofEpochMilli(denylistEntry.addedAt)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.RFC_1123_DATE_TIME)

        val embed = Embed(
            title = "Community Ban",
            color = 0xFF0000,
            fields = listOf(
                EmbedField("User", "${user.name} (${user.uuid})", inline = true),
                EmbedField("Reason", denylistEntry.reason),
                EmbedField("Word Triggered", denylistEntry.word, inline = true),
                EmbedField("Added By", denylistEntry.addedBy, inline = true),
                EmbedField("Action", denylistEntry.action.name, inline = true),
                EmbedField("Timestamp", timestamp)
            )
        )

        val payload = WebhookPayload(listOf(embed))
        val jsonPayload = gson.toJson(payload)
        val requestBody =
            jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    error("Failed to send discord embed: status=${response.code}, message=${response.message}")
                }
            }
        } catch (e: Exception) {
            error("Error sending Discord webhook: ${e.message}")
        }
    }
}