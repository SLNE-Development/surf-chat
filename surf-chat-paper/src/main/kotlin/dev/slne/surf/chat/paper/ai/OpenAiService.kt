package dev.slne.surf.chat.paper.ai

import com.github.benmanes.caffeine.cache.Caffeine
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import com.openai.models.moderations.Moderation
import com.openai.models.moderations.ModerationCreateParams
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.chat.paper.config.AiModerationThresholds
import dev.slne.surf.chat.paper.config.aiModerationConfig
import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
import kotlinx.coroutines.future.await
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

val openAiService = OpenAiService()

class OpenAiService {
    private val client = OpenAIOkHttpClientAsync.builder().apiKey(aiModerationConfig.apiKey).build()

    private val scoreCache = Caffeine
        .newBuilder()
        .expireAfterWrite(3.hours.toJavaDuration())
        .asLoadingCache<ModerationInput, Object2DoubleMap<Category>> {
            fetchCategoryScores(it)
        }

    suspend fun classifyChatMessage(
        message: String,
        messageType: String
    ): ClassificationResult {
        val config = aiModerationConfig
        val categoryScores = scoreCache.get(
            ModerationInput(
                model = config.model,
                message = message,
                messageType = messageType
            )
        )
        val matchedScores = Object2DoubleOpenHashMap<Category>()

        for (category in Category.entries) {
            val threshold = category.threshold(config.thresholds) ?: continue
            val score = categoryScores.getDouble(category)

            if (score >= threshold.coerceIn(0.0, 1.0)) {
                matchedScores.put(category, score)
            }
        }

        val action = matchedScores.keys
            .maxByOrNull { it.action.ordinal }
            ?.action
            ?: ClassificationAction.NONE

        return ClassificationResult(action, matchedScores, categoryScores)
    }

    private suspend fun fetchCategoryScores(
        input: ModerationInput
    ): Object2DoubleMap<Category> {
        val params = ModerationCreateParams.builder()
            .model(input.model)
            .input(
                buildString {
                    appendLine("[GAME_CHAT: MINECRAFT]")
                    appendLine("[MESSAGE_TYPE: ${input.messageType}]")
                    append(input.message)
                }
            )
            .build()

        val result = client.moderations()
            .create(params)
            .await()
            .results()
            .single()

        return Category.categoryScores(result.categoryScores())
    }

    private data class ModerationInput(
        val model: String,
        val message: String,
        val messageType: String
    )

    data class ClassificationResult(
        val action: ClassificationAction,
        val matchedScores: Object2DoubleMap<Category>,
        val categoryScores: Object2DoubleMap<Category>
    )

    enum class ClassificationAction {
        NONE,
        SILENT_FLAG,
        DELETE,
        SEVERE
    }

    enum class Category(val action: ClassificationAction) {
        HARASSMENT(ClassificationAction.DELETE),
        HARASSMENT_THREATENING(ClassificationAction.DELETE),
        HATE(ClassificationAction.DELETE),
        HATE_THREATENING(ClassificationAction.SEVERE),
        ILLICIT(ClassificationAction.SILENT_FLAG),
        ILLICIT_VIOLENT(ClassificationAction.SILENT_FLAG),
        SELF_HARM(ClassificationAction.SILENT_FLAG),
        SELF_HARM_INSTRUCTIONS(ClassificationAction.SILENT_FLAG),
        SELF_HARM_INTENT(ClassificationAction.SILENT_FLAG),
        SEXUAL(ClassificationAction.DELETE),
        SEXUAL_MINORS(ClassificationAction.SEVERE),
        VIOLENCE(ClassificationAction.NONE),
        VIOLENCE_GRAPHIC(ClassificationAction.DELETE);

        fun threshold(thresholds: AiModerationThresholds): Double? = when (this) {
            HARASSMENT -> thresholds.harassment
            HARASSMENT_THREATENING -> thresholds.harassmentThreatening
            HATE -> thresholds.hate
            HATE_THREATENING -> thresholds.hateThreatening
            ILLICIT -> thresholds.illicit
            ILLICIT_VIOLENT -> thresholds.illicitViolent
            SELF_HARM -> thresholds.selfHarm
            SELF_HARM_INSTRUCTIONS -> thresholds.selfHarmInstructions
            SELF_HARM_INTENT -> thresholds.selfHarmIntent
            SEXUAL -> thresholds.sexual
            SEXUAL_MINORS -> thresholds.sexualMinors
            VIOLENCE -> null
            VIOLENCE_GRAPHIC -> thresholds.violenceGraphic
        }

        companion object {
            fun categoryScores(
                scores: Moderation.CategoryScores
            ): Object2DoubleMap<Category> {
                val map = Object2DoubleOpenHashMap<Category>(entries.size)

                map.put(HARASSMENT, scores.harassment())
                map.put(HARASSMENT_THREATENING, scores.harassmentThreatening())
                map.put(HATE, scores.hate())
                map.put(HATE_THREATENING, scores.hateThreatening())
                map.put(ILLICIT, scores.illicit())
                map.put(ILLICIT_VIOLENT, scores.illicitViolent())
                map.put(SELF_HARM, scores.selfHarm())
                map.put(SELF_HARM_INSTRUCTIONS, scores.selfHarmInstructions())
                map.put(SELF_HARM_INTENT, scores.selfHarmIntent())
                map.put(SEXUAL, scores.sexual())
                map.put(SEXUAL_MINORS, scores.sexualMinors())
                map.put(VIOLENCE, scores.violence())
                map.put(VIOLENCE_GRAPHIC, scores.violenceGraphic())

                return map
            }
        }
    }
}
