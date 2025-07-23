@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.chat.bukkit.dialog

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.notificationService
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.registry.data.dialog.DialogBase

fun settingsDialog() = dialog {
    base {
        title { primary("Chat - Einstellungen") }
        preventClosingWithEscape()
        afterAction(DialogBase.DialogAfterAction.NONE)
        input {
            simpleBoolean("pings", buildText {
                info("Benachrichtigungen bei Erwähnungen")
            }, true)
        }
    }

    type {
        confirmation(createSaveButton(), createCloseButton())
    }
}

private fun createSaveButton() = actionButton {
    label { success("Speichern") }
    action {
        customPlayerClick { response, viewer ->

            plugin.launch {
                response.getBoolean("pings").let {
                    it?.let {
                        if (it) {
                            notificationService.enablePings(viewer.uniqueId)
                        } else {
                            notificationService.disablePings(viewer.uniqueId)
                        }
                    }
                }

                viewer.showDialog(createSettingsSavedNotice())
            }
        }
    }
}

private fun createSettingsSavedNotice() = dialog {
    base {
        title { primary("Chat — Einstellungen gespeichert") }
        afterAction(DialogBase.DialogAfterAction.NONE)
        body {
            plainMessage {
                success("Die Einstellungen wurden erfolgreich gespeichert.")
            }
        }
    }
    type {
        notice(createCloseButton())
    }
}

private fun createCloseButton() = actionButton {
    label { error("Abbrechen") }
    action {
        playerCallback { it.clearDialogs() }
    }
}