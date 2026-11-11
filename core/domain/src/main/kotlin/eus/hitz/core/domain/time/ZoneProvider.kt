package eus.hitz.core.domain.time

import java.time.Instant
import java.time.ZoneId

/** The learner's time zone: days (the daily goal, the week in the profile) follow local midnight. */
fun interface ZoneProvider {
    fun zone(): ZoneId
}

/** Local day of an instant, as days since 1970-01-01. */
fun epochDay(epochMillis: Long, zone: ZoneId): Long =
    Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate().toEpochDay()
