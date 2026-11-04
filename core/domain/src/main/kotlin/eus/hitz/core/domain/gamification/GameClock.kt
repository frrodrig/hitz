package eus.hitz.core.domain.gamification

import eus.hitz.core.domain.time.EpochClock
import eus.hitz.core.domain.time.ZoneProvider
import eus.hitz.core.domain.time.epochDay
import java.time.LocalDate
import javax.inject.Inject

/** Current time and local day for gamification rules. */
class GameClock @Inject constructor(
    private val clock: EpochClock,
    private val zones: ZoneProvider,
) {
    fun now(): Long = clock.now()

    fun today(): Long = epochDay(clock.now(), zones.zone())

    /** Share of the league week starting on [weekStart] that has passed, from 0 to 1. */
    fun weekElapsed(weekStart: Long): Double {
        val start = LocalDate.ofEpochDay(weekStart).atStartOfDay(zones.zone()).toInstant().toEpochMilli()
        return ((now() - start).toDouble() / WEEK_MILLIS).coerceIn(0.0, 1.0)
    }

    private companion object {
        const val WEEK_MILLIS = Leagues.DAYS_PER_WEEK * 24 * 60 * 60 * 1000.0
    }
}
