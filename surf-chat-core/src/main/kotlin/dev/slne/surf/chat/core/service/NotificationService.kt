package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

/**
 * Service interface for managing notification preferences for users.
 *
 * This service provides methods to enable or disable notifications such as pings and invites
 * for individual users, as well as check the current status of these notification preferences.
 * The implementation includes database-backed functionality to persist user preferences.
 */
interface NotificationService : DatabaseTableHolder {
    /**
     * Checks if the ping notifications are enabled for the user identified by the given UUID.
     *
     * @param uuid The unique identifier of the user whose ping notification status is being checked.
     * @return `true` if ping notifications are enabled for the specified user, `false` otherwise.
     */
    suspend fun pingsEnabled(uuid: UUID): Boolean

    /**
     * Enables the ping notifications for the user identified by their UUID.
     *
     * @param uuid The unique identifier of the user for whom ping notifications will be enabled.
     */
    suspend fun enablePings(uuid: UUID)

    /**
     * Disables pings for the user associated with the given UUID.
     *
     * This method updates the user's notification settings to turn off
     * ping notifications, preventing them from receiving future ping alerts.
     *
     * @param uuid The unique identifier of the user whose ping notifications are to be disabled.
     */
    suspend fun disablePings(uuid: UUID)

    /**
     * Checks if the user with the given UUID has enabled invites.
     *
     * @param uuid The unique identifier of the user for whom to check the invite status.
     * @return `true` if invites are enabled for the specified user, `false` otherwise.
     */
    suspend fun invitesEnabled(uuid: UUID): Boolean

    /**
     * Enables the ability to receive invites for the specified user.
     *
     * @param uuid The unique identifier of the user for whom invites are being enabled.
     */
    suspend fun enableInvites(uuid: UUID)

    /**
     * Disables invite notifications for a specific user identified by their UUID.
     *
     * This method updates the user's notification settings to prevent them
     * from receiving further invite-related notifications.
     *
     * @param uuid The unique identifier of the user for whom invite notifications should be disabled.
     */
    suspend fun disableInvites(uuid: UUID)

    /**
     * Companion object for accessing the singleton instance of the NotificationService.
     * Provides centralized access to the functionalities of the NotificationService.
     */
    companion object {
        /**
         * Singleton instance of the NotificationService interface.
         *
         * Provides centralized access to the NotificationService, which is responsible
         * for managing user notifications, including enabling or disabling
         * pings and invites for specific users.
         */
        val INSTANCE = requiredService<NotificationService>()
    }
}

/**
 * A property to access the singleton instance of the NotificationService.
 * NotificationService is responsible for managing notifications within the system,
 * providing functionalities such as sending, managing, or broadcasting notifications
 * to users or systems as required.
 */
val notificationService get() = NotificationService.INSTANCE