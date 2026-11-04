package eus.hitz.core.domain.gamification

/** What kind of session earned XP. */
enum class ActivityKind { Lesson, Story, Review, Legendary }

/** A finished session. [epochDay] is the learner's local day. */
data class Activity(
    val sessionId: String,
    val kind: ActivityKind,
    val accuracy: Float,
    val perfect: Boolean,
    val xp: Int,
    val completedAt: Long,
    val epochDay: Long,
)

enum class StreakDayKind { Practiced, Frozen }

/** Spendable and regenerating resources. Hearts are stored as of [heartsUpdatedAt]. */
data class Wallet(
    val gems: Int = INITIAL_GEMS,
    val hearts: Int = HeartRules.MAX_HEARTS,
    val heartsUpdatedAt: Long = 0,
    val streakFreezes: Int = 0,
) {
    companion object {
        const val INITIAL_GEMS = 20
    }
}

enum class LeagueOutcome { Promoted, Stayed, Demoted }

/** The learner's league: [tier] (0 = lowest) for the week starting on [weekStart] (epoch day). */
data class LeagueState(
    val tier: Int = 0,
    val weekStart: Long = NOT_JOINED,
    val lastRank: Int? = null,
    val lastOutcome: LeagueOutcome? = null,
) {
    companion object {
        const val NOT_JOINED = -1L
    }
}

/** Everything gamification is computed from. */
data class PlayerData(
    val wallet: Wallet = Wallet(),
    val league: LeagueState = LeagueState(),
    val activities: List<Activity> = emptyList(),
    val streakDays: Map<Long, StreakDayKind> = emptyMap(),
    val claimed: Set<String> = emptySet(),
)
