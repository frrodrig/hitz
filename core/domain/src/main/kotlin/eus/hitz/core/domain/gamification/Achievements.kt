// Game balance tables: the numbers are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.gamification

/** Achievements with growing tiers; each tier reached grants [Achievements.tierGems] gems once. */
enum class AchievementType(val thresholds: List<Int>) {
    Streak(listOf(3, 7, 14, 30)),
    Xp(listOf(100, 500, 1000, 5000)),
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

object Achievements {
    private val TierGems = listOf(5, 10, 20, 40)

    fun tierGems(tier: Int): Int = TierGems[tier - 1]

    fun progress(data: PlayerData): List<AchievementProgress> {
        val activities = data.activities
        return AchievementType.entries.map { type ->
            val value = when (type) {
                AchievementType.Streak -> StreakRules.longest(data.streakDays)
                AchievementType.Xp -> activities.sumOf { it.xp }
                AchievementType.Lessons -> activities.count { it.kind == ActivityKind.Lesson }
                AchievementType.Perfect -> activities.count { it.perfect }
                AchievementType.Stories -> activities.count { it.kind == ActivityKind.Story }
            }
            AchievementProgress(type, value)
        }
    }

    /** Reward id of reaching [tier] (1-based). */
    fun rewardId(type: AchievementType, tier: Int): String = "achievement:${type.name}:$tier"
}
