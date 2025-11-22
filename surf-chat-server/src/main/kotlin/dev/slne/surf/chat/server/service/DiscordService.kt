package dev.slne.surf.chat.server.service

import com.google.gson.Gson
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service

@Service
class DiscordService {
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun sendCommunityBanNotification(
        url: String,
        user: OfflineCloudPlayer,
        denylistEntry: DenylistEntry,
        punishmentId: String
    ) = withContext(Dispatchers.IO) {
        val jsonPayload = "{\n" +
                "  \"content\": \"\",\n" +
                "  \"tts\": false,\n" +
                "  \"embeds\": [\n" +
                "    {\n" +
                "      \"id\": 880260049,\n" +
                "      \"description\": \"Der Spieler ${user.name()} wurde aufgrund eines automatisch erkannten Wortes, welches auf der Blacklist steht, bestraft.\\n\\nSollte dies nicht richtig sein, melde dich bitte bei einem Developer.\",\n" +
                "      \"fields\": [\n" +
                "        {\n" +
                "          \"id\": 384733282,\n" +
                "          \"name\": \"Kategorie\",\n" +
                "          \"value\": \"${denylistEntry.reason}\",\n" +
                "          \"inline\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 449694866,\n" +
                "          \"name\": \"Erkanntes Wort\",\n" +
                "          \"value\": \"${denylistEntry.word}\",\n" +
                "          \"inline\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 870510395,\n" +
                "          \"name\": \"Hinzugefügt von\",\n" +
                "          \"value\": \"${denylistEntry.addedBy}\",\n" +
                "          \"inline\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 594709829,\n" +
                "          \"name\": \"Aktionsname\",\n" +
                "          \"value\": \"${denylistEntry.action.name}\",\n" +
                "          \"inline\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 780176883,\n" +
                "          \"name\": \"Punishment-ID\",\n" +
                "          \"value\": \"$punishmentId\",\n" +
                "          \"inline\": true\n" +
                "        }\n" +
                "      ],\n" +
                "      \"color\": 15818334,\n" +
                "      \"footer\": {\n" +
                "        \"text\": \"automated punishment by surf-chat\"\n" +
                "      },\n" +
                "      \"title\": \"Der Spieler ${user.name()} wurde bestraft\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"components\": [],\n" +
                "  \"actions\": {},\n" +
                "  \"flags\": 0,\n" +
                "  \"username\": \"Chat Punishments\"\n" +
                "}"
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