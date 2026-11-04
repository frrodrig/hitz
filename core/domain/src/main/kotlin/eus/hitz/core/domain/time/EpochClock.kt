package eus.hitz.core.domain.time

/** Current time in epoch milliseconds; injected so tests control time. */
fun interface EpochClock {
    fun now(): Long
}
