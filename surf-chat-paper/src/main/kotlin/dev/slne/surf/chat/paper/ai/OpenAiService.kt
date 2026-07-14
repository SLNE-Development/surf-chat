package dev.slne.surf.chat.paper.ai

import com.github.benmanes.caffeine.cache.Caffeine
import com.openai.client.okhttp.OpenAIOkHttpClientAsync
import com.openai.models.moderations.Moderation
import com.openai.models.moderations.ModerationCreateParams
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.chat.core.common.aimoderation.ModerationCategory
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationAction
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.paper.config.AiModerationThresholds
import dev.slne.surf.chat.paper.config.aiModerationConfig
import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
import kotlinx.coroutines.future.await
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

object OpenAiService {
    private val client = OpenAIOkHttpClientAsync.builder()
        .apiKey(aiModerationConfig.apiKey)
        .logLevel(aiModerationConfig.logLevel)
        .build()

    private val scoreCache = Caffeine
        .newBuilder()
        .expireAfterWrite(3.hours.toJavaDuration())
        .asLoadingCache<ModerationInput, Object2DoubleMap<ModerationCategory>> {
            fetchCategoryScores(it)
        }

    suspend fun classifyChatMessage(
        message: String,
        messageType: String
    ): ModerationClassificationResult {
        val config = aiModerationConfig
        val categoryScores = scoreCache.get(
            ModerationInput(
                model = config.model,
                message = message,
                messageType = messageType
            )
        )

        val flaggedScores = Object2DoubleOpenHashMap<ModerationCategory>()
        for (category in ModerationCategory.entries) {
            val threshold = category.threshold(config.thresholds) ?: continue
            val score = categoryScores.getDouble(category)

            if (score >= threshold.coerceIn(0.0, 1.0)) {
                flaggedScores.put(category, score)
            }
        }

        return ModerationClassificationResult(decideAction(flaggedScores.keys), flaggedScores)
    }

    private suspend fun fetchCategoryScores(
        input: ModerationInput
    ): Object2DoubleMap<ModerationCategory> {
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

        return categoryScoresByAi(result.categoryScores())
    }

    private fun decideAction(categories: Set<ModerationCategory>): ModerationClassificationAction {
        for (action in ModerationClassificationAction.reversedEntries) {
            if (action.categories.any { categories.contains(it) }) return action
        }

        return ModerationClassificationAction.NONE
    }

    private fun categoryScoresByAi(scores: Moderation.CategoryScores): Object2DoubleMap<ModerationCategory> {
        val map = Object2DoubleOpenHashMap<ModerationCategory>()

        map.put(ModerationCategory.HARASSMENT, scores.harassment())
        map.put(ModerationCategory.HARASSMENT_THREATENING, scores.harassmentThreatening())
        map.put(ModerationCategory.HATE, scores.hate())
        map.put(ModerationCategory.HATE_THREATENING, scores.hateThreatening())
        map.put(ModerationCategory.ILLICIT, scores.illicit())
        map.put(ModerationCategory.ILLICIT_VIOLENT, scores.illicitViolent())
        map.put(ModerationCategory.SELF_HARM, scores.selfHarm())
        map.put(ModerationCategory.SELF_HARM_INSTRUCTIONS, scores.selfHarmInstructions())
        map.put(ModerationCategory.SELF_HARM_INTENT, scores.selfHarmIntent())
        map.put(ModerationCategory.SEXUAL, scores.sexual())
        map.put(ModerationCategory.SEXUAL_MINORS, scores.sexualMinors())
        map.put(ModerationCategory.VIOLENCE, scores.violence())
        map.put(ModerationCategory.VIOLENCE_GRAPHIC, scores.violenceGraphic())

        return map
    }

    private fun ModerationCategory.threshold(thresholds: AiModerationThresholds): Double? = when (this) {
        ModerationCategory.HARASSMENT -> thresholds.harassment
        ModerationCategory.HARASSMENT_THREATENING -> thresholds.harassmentThreatening
        ModerationCategory.HATE -> thresholds.hate
        ModerationCategory.HATE_THREATENING -> thresholds.hateThreatening
        ModerationCategory.ILLICIT -> thresholds.illicit
        ModerationCategory.ILLICIT_VIOLENT -> thresholds.illicitViolent
        ModerationCategory.SELF_HARM -> thresholds.selfHarm
        ModerationCategory.SELF_HARM_INSTRUCTIONS -> thresholds.selfHarmInstructions
        ModerationCategory.SELF_HARM_INTENT -> thresholds.selfHarmIntent
        ModerationCategory.SEXUAL -> thresholds.sexual
        ModerationCategory.SEXUAL_MINORS -> thresholds.sexualMinors
        ModerationCategory.VIOLENCE -> null
        ModerationCategory.VIOLENCE_GRAPHIC -> thresholds.violenceGraphic
    }

    private data class ModerationInput(
        val model: String,
        val message: String,
        val messageType: String
    )
}
