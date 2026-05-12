package dev.slne.surf.chat.paper.service

import dev.slne.surf.chat.paper.plugin
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicInteger

/**
 * Service responsible for tracking recent player joins and quits in order to
 * automatically disable connection messages during connection spikes.
 *
 * A sliding one-minute window is used to determine whether the configured
 * threshold has been exceeded.
 */
object ConnectionMessageService {
    /**
     * Stores connection event timestamps in nanoseconds (joins + quits).
     *
     * Entries are ordered from oldest to newest.
     */
    private val joinTimestamps = ConcurrentLinkedDeque<Long>()

    /**
     * Cached amount of currently valid connection events inside the sliding time window.
     *
     * Using an [AtomicInteger] avoids the expensive O(n) `Deque.size` call.
     */
    private val joinCount = AtomicInteger(0)

    /**
     * Duration of the sliding rate-limit window.
     */
    private const val WINDOW_NANO = 60_000_000_000L // 1 minute

    /**
     * Records a player connection event (join or quit) and updates the current sliding window state.
     */
    fun recordEvent() {
        val now = System.nanoTime()

        joinTimestamps.addLast(now)
        joinCount.incrementAndGet()

        pruneOldTimestamps(now)
    }

    /**
     * Returns whether the configured join threshold has been exceeded.
     *
     * Old timestamps are cleaned up before evaluating the threshold.
     *
     * @return `true` if the join rate exceeds the configured limit,
     * otherwise `false`
     */
    fun isRateLimitExceeded(): Boolean {
        val config = plugin.connectionMessageConfig
        if (!config.autoDisableOnHighPlayerJoinThreshold) return false

        pruneOldTimestamps(System.nanoTime())

        return joinCount.get() >= config.joinsPerMinuteThreshold
    }

    /**
     * Determines whether connection messages should currently be shown.
     *
     * Connection messages are hidden if:
     * - the feature is disabled in the configuration
     * - the join rate limit is exceeded
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
     * Every removed timestamp also decrements the cached join counter.
     *
     * @param now the current timestamp in nanoseconds
     */
    private fun pruneOldTimestamps(now: Long) {
        val cutoff = now - WINDOW_NANO

        while (true) {
            val first = joinTimestamps.peekFirst() ?: break
            if (first >= cutoff) break

            if (joinTimestamps.pollFirst() != null) {
                joinCount.decrementAndGet()
            }
        }
    }
}
