package dev.slne.surf.chat.paper.service

import dev.slne.surf.chat.paper.plugin
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue

/**
 * Service responsible for tracking recent player connection events in order to
 * automatically disable connection messages during connection spikes.
 *
 * A sliding one-minute window is used to determine whether the configured
 * threshold has been reached.
 */
object ConnectionMessageService {
    private val lock = Any()

    /**
     * Stores connection event timestamps in nanoseconds.
     *
     * Entries are ordered from oldest to newest.
     */
    private val eventTimestamps = LongArrayFIFOQueue()

    /**
     * Duration of the sliding rate-limit window.
     */
    private const val WINDOW_NANOS = 60_000_000_000L // 1 minute

    /**
     * Records a player connection event and updates the current sliding window state.
     */
    fun recordEvent() {
        val now = System.nanoTime()

        synchronized(lock) {
            eventTimestamps.enqueue(now)
            pruneOldTimestamps(now)
        }
    }

    /**
     * Returns whether connection messages should currently be suppressed because
     * the configured threshold has been reached.
     *
     * Old timestamps are cleaned up before evaluating the threshold.
     *
     * @return `true` if the amount of recent connection events is at or above
     * the configured threshold, otherwise `false`
     */
    fun isRateLimitExceeded(): Boolean {
        val config = plugin.connectionMessageConfig
        if (!config.autoDisableOnHighConnectionEventThreshold) return false

        return synchronized(lock) {
            pruneOldTimestamps(System.nanoTime())
            eventTimestamps.size() >= config.connectionEventsPerMinuteThreshold
        }
    }

    /**
     * Determines whether connection messages should currently be shown.
     *
     * Connection messages are hidden if:
     * - the feature is disabled in the configuration
     * - the connection event threshold has been reached
     *
     * @return `true` if connection messages should be displayed,
     * otherwise `false`
     */
    fun shouldShowConnectionMessage(): Boolean {
        val config = plugin.connectionMessageConfig
        return config.enabled && !isRateLimitExceeded()
    }

    /**
     * Removes timestamps that are outside the sliding one-minute window.
     *
     * @param now the current timestamp in nanoseconds
     */
    private fun pruneOldTimestamps(now: Long) {
        val cutoff = now - WINDOW_NANOS

        while (!eventTimestamps.isEmpty && eventTimestamps.firstLong() < cutoff) {
            eventTimestamps.dequeueLong()
        }
    }
}
