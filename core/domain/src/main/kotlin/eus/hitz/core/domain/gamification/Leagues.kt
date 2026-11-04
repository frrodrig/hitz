package eus.hitz.core.domain.gamification

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.pow
import kotlin.random.Random

/** A row of the weekly ranking. [rank] is 1-based. */
data class Standing(val rank: Int, val name: String, val xp: Int, val isLearner: Boolean)

/**
 * Weekly leagues, simulated on the device (offline-first): the learner competes with rivals that
 * are generated from the week and the tier, so the ranking is stable while it moves through the
 * week. Rivals earn XP along a personal curve; higher tiers have stronger rivals.
 */
object Leagues {
    const val TIERS = 6
    const val GROUP_SIZE = 15
    const val PROMOTED = 5
    const val DEMOTED = 3
    const val DAYS_PER_WEEK = 7
    private val RankGems = listOf(20, 15, 10)
    private val MeanWeeklyXp = listOf(40, 70, 110, 160, 220, 300)
    private const val MIN_FACTOR = 0.1
    private const val FACTOR_RANGE = 1.9
    private const val MIN_EXPONENT = 0.6
    private const val EXPONENT_RANGE = 1.0

    /** Rival names: common Basque first names with an initial. */
    private val Names = listOf(
        "Ane", "Iker", "Maialen", "Jon", "Itziar", "Mikel", "Nerea", "Unai", "Amaia", "Aitor",
        "Leire", "Asier", "Uxue", "Gorka", "Irati", "Eneko", "June", "Oier", "Haizea", "Julen",
        "Garazi", "Ander", "Nahia", "Xabier", "Alaitz", "Ibai", "Izaro", "Markel", "Olatz", "Peio",
    )
    private const val INITIALS = "ABDEGIKLMOPSTUZ"

    /** Monday of the week of [epochDay]. */
    fun weekStart(epochDay: Long): Long =
        LocalDate.ofEpochDay(epochDay).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toEpochDay()

    fun gemsForRank(rank: Int): Int = RankGems.getOrElse(rank - 1) { 0 }

    /**
     * The ranking of week [weekStart] in [tier] when [elapsed] (0 to 1) of the week has passed and the
     * learner has [learnerXp]. Ties go to the learner, whose row has an empty name (the UI names it).
     */
    fun standings(weekStart: Long, tier: Int, learnerXp: Int, elapsed: Double): List<Standing> {
        val random = Random(weekStart * TIERS + tier)
        val names = Names.shuffled(random)
        val rivals = (0 until GROUP_SIZE - 1).map { index ->
            val target = MeanWeeklyXp[tier] * (MIN_FACTOR + random.nextDouble() * FACTOR_RANGE)
            val exponent = MIN_EXPONENT + random.nextDouble() * EXPONENT_RANGE
            val name = "${names[index]} ${INITIALS[random.nextInt(INITIALS.length)]}."
            name to (target * elapsed.coerceIn(0.0, 1.0).pow(exponent)).toInt()
        }
        val entries = rivals.map { (name, xp) -> Triple(name, xp, false) } + Triple("", learnerXp, true)
        return entries
            .sortedWith(compareByDescending<Triple<String, Int, Boolean>> { it.second }.thenByDescending { it.third })
            .mapIndexed { index, (name, xp, isLearner) -> Standing(index + 1, name, xp, isLearner) }
    }

    fun outcome(rank: Int, tier: Int): LeagueOutcome = when {
        rank <= PROMOTED && tier < TIERS - 1 -> LeagueOutcome.Promoted
        rank > GROUP_SIZE - DEMOTED && tier > 0 -> LeagueOutcome.Demoted
        else -> LeagueOutcome.Stayed
    }

    fun nextTier(tier: Int, outcome: LeagueOutcome): Int = when (outcome) {
        LeagueOutcome.Promoted -> tier + 1
        LeagueOutcome.Stayed -> tier
        LeagueOutcome.Demoted -> tier - 1
    }

    fun rewardId(weekStart: Long): String = "league:$weekStart"
}
