// The numbers are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.gamification

/** Achievements with growing tiers, shown as badges in the profile. */
enum class AchievementType(val thresholds: List<Int>) {
    /** Days the daily goal was met (not necessarily in a row: missing a day loses nothing). */
    GoalDays(listOf(3, 7, 30, 100)),

    /** Minutes of active study. */
    StudyMinutes(listOf(60, 300, 600, 1500)),
    Lessons(listOf(5, 25, 50, 100)),
    Perfect(listOf(1, 10, 25, 50)),
    Stories(listOf(1, 5, 10, 24)),
}

data class AchievementProgress(val type: AchievementType, val value: Int) {
    /** Tiers reached, from 0 to the number of thresholds. */
    val tier: Int get() = type.thresholds.count { value >= it }

    /** The next goal, or null when every tier is reached. */
    val nextTarget: Int? get() = type.thresholds.getOrNull(tier)
}

/** A tier reached by a session. */
data class AchievementTier(val type: AchievementType, val tier: Int)

object Achievements {
    fun progress(data: PlayerData): List<AchievementProgress> {
        val activities = data.activities
        return AchievementType.entries.map { type ->
            val value = when (type) {
                AchievementType.GoalDays -> data.studyDays.values.count { it.goalMet }
                AchievementType.StudyMinutes -> (data.studyDays.values.sumOf { it.activeMillis } / 60_000).toInt()
                AchievementType.Lessons -> activities.count { it.kind == ActivityKind.Lesson }
                AchievementType.Perfect -> activities.count { it.perfect }
                AchievementType.Stories -> activities.count { it.kind == ActivityKind.Story }
            }
            AchievementProgress(type, value)
        }
    }

    /** Tiers in [after] that [before] had not reached. */
    fun reached(before: List<AchievementProgress>, after: List<AchievementProgress>): List<AchievementTier> =
        after.flatMap { progress ->
            val previous = before.firstOrNull { it.type == progress.type }?.tier ?: 0
            (previous + 1..progress.tier).map { AchievementTier(progress.type, it) }
        }
}
