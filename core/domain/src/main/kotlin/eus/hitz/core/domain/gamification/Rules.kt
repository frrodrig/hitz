// Game balance tables: the numbers are the rules.
@file:Suppress("MagicNumber")

package eus.hitz.core.domain.gamification

/** XP per finished session: a base by kind, plus a bonus for a perfect session. */
object XpRules {
    const val PERFECT_BONUS = 5

    fun base(kind: ActivityKind): Int = when (kind) {
        ActivityKind.Lesson -> 10
        ActivityKind.Story -> 15
        ActivityKind.Review -> 10
        ActivityKind.Legendary -> 20
    }

    fun xp(kind: ActivityKind, perfect: Boolean): Int = base(kind) + if (perfect) PERFECT_BONUS else 0
}

/**
 * Shared hearts: lessons cost one per wrong answer, one heart comes back every
 * [REGEN_MILLIS] up to [MAX_HEARTS], and a finished review gives one.
 */
object HeartRules {
    const val MAX_HEARTS = 5
    const val REGEN_MILLIS = 4 * 60 * 60 * 1000L

    /** The wallet with the hearts regenerated until [now]. */
    fun regenerate(wallet: Wallet, now: Long): Wallet {
        if (wallet.hearts >= MAX_HEARTS) return wallet.copy(hearts = MAX_HEARTS, heartsUpdatedAt = now)
        val regained = ((now - wallet.heartsUpdatedAt) / REGEN_MILLIS).toInt().coerceAtLeast(0)
        val hearts = minOf(MAX_HEARTS, wallet.hearts + regained)
        val updatedAt = if (hearts == MAX_HEARTS) now else wallet.heartsUpdatedAt + regained * REGEN_MILLIS
        return wallet.copy(hearts = hearts, heartsUpdatedAt = updatedAt)
    }

    /** When the next heart comes back, or null when hearts are full. */
    fun nextHeartAt(wallet: Wallet, now: Long): Long? {
        val current = regenerate(wallet, now)
        return if (current.hearts >= MAX_HEARTS) null else current.heartsUpdatedAt + REGEN_MILLIS
    }

    fun lose(wallet: Wallet, now: Long): Wallet {
        val current = regenerate(wallet, now)
        // With full hearts, regenerate() has just reset the clock: losing one starts it.
        return current.copy(hearts = (current.hearts - 1).coerceAtLeast(0))
    }

    fun gain(wallet: Wallet, now: Long, amount: Int = 1): Wallet {
        val current = regenerate(wallet, now)
        return current.copy(hearts = minOf(MAX_HEARTS, current.hearts + amount))
    }
}

/**
 * Streaks count practiced days in a row, up to today or yesterday. A frozen day keeps the streak
 * alive without adding to it; freezes are spent automatically on missed days.
 */
object StreakRules {
    const val MAX_FREEZES = 2

    fun current(days: Map<Long, StreakDayKind>, today: Long): Int {
        var day = if (today in days) today else today - 1
        var count = 0
        while (day in days) {
            if (days[day] == StreakDayKind.Practiced) count++
            day--
        }
        return count
    }

    fun longest(days: Map<Long, StreakDayKind>): Int {
        var best = 0
        var run = 0
        var previous = Long.MIN_VALUE
        days.keys.sorted().forEach { day ->
            if (previous == Long.MIN_VALUE || day != previous + 1) run = 0
            if (days[day] == StreakDayKind.Practiced) run++
            best = maxOf(best, run)
            previous = day
        }
        return best
    }

    /** Missed days before [today] that freezes can cover; empty if they cannot cover them all. */
    fun daysToFreeze(days: Map<Long, StreakDayKind>, today: Long, freezes: Int): List<Long> {
        val last = days.keys.maxOrNull() ?: return emptyList()
        val missed = (last + 1 until today).toList()
        val worthKeeping = current(days, last) > 0
        return if (worthKeeping && missed.isNotEmpty() && missed.size <= freezes) missed else emptyList()
    }
}

/** Items of the shop, paid with gems. */
enum class ShopItem(val price: Int) {
    /** Fills the hearts up. */
    HeartRefill(30),

    /** Keeps the streak alive for one missed day; up to [StreakRules.MAX_FREEZES]. */
    StreakFreeze(20),
}
