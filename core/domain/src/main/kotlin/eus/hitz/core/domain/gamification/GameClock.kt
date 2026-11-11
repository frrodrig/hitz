package eus.hitz.core.domain.gamification

import eus.hitz.core.domain.time.EpochClock
import eus.hitz.core.domain.time.ZoneProvider
import eus.hitz.core.domain.time.epochDay
import javax.inject.Inject

/** Current time and local day for the daily goal. */
class GameClock @Inject constructor(
    private val clock: EpochClock,
    private val zones: ZoneProvider,
) {
    fun now(): Long = clock.now()

    fun today(): Long = epochDay(clock.now(), zones.zone())
}
