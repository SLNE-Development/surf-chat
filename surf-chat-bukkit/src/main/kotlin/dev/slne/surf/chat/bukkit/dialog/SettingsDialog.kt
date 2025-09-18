@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.chat.bukkit.dialog

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.directMessageService
import dev.slne.surf.chat.core.service.notificationService
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase
import java.util.*

suspend fun settingsDialog(uuid: UUID): Dialog {
    val pingsEnabled = notificationService.pingsEnabled(uuid)
    val invitesEnabled = notificationService.invitesEnabled(uuid)
    val directMessagesEnabled = directMessageService.directMessagesEnabled(uuid)

    return dialog {
        base {
            title { primary("Chat - Einstellungen") }
            afterAction(DialogBase.DialogAfterAction.NONE)
            input {
                simpleBoolean("pings", buildText {
                    info("Benachrichtigungen bei Erwähnungen")
                }, pingsEnabled)
                simpleBoolean("invites", buildText {
                    info("Einladungen für Nachrichtenkanäle")
                }, invitesEnabled)
                simpleBoolean("dms", buildText {
                    info("Direktnachrichten")
                }, directMessagesEnabled)
            }
        }

        type {
            confirmation(createSaveButton(), createCloseButton())
        }
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

                response.getBoolean("dms").let {
                    it?.let {
                        if (it) {
                            directMessageService.enableDirectMessages(viewer.uniqueId)
                        } else {
                            directMessageService.disableDirectMessages(viewer.uniqueId)
                        }
                    }
                }

                response.getBoolean("invites").let {
                    it?.let {
                        if (it) {
                            notificationService.enableInvites(viewer.uniqueId)
                        } else {
                            notificationService.disableInvites(viewer.uniqueId)
                        }
                    }
                }

                viewer.clearDialogs()
            }
        }
    }
}

private fun createCloseButton() = actionButton {
    label { error("Schließen") }
    action {
        playerCallback { it.clearDialogs() }
    }
}