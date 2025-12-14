package dev.slne.surf.chat.paper.command.surfchat.ai

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.paper.config.AiModerationConfig
import dev.slne.surf.chat.paper.config.aiModerationConfig
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer

fun aiModerationCommand() = subcommand("ai-moderation") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_AI_MODERATION)

    subcommand("reload") {
        anyExecutor { sender, _ ->
            AiModerationConfig.reload()
            sender.sendMessage("Ai Moderation Config Reloaded!")
        }
    }

    subcommand("enable") {
        withRequirement { !aiModerationConfig.enabled }
        anyExecutor { sender, arguments ->
            aiModerationConfig.enabled = true
            AiModerationConfig.save()
            sender.sendMessage("Ai Moderation Enabled!")
            forEachPlayer { it.updateCommands() }
        }
    }

    subcommand("disable") {
        withRequirement { aiModerationConfig.enabled }
        anyExecutor { sender, arguments ->
            aiModerationConfig.enabled = false
            AiModerationConfig.save()
            sender.sendMessage("Ai Moderation Disabled!")
        }
    }
}